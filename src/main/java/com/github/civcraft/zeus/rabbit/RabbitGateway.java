package com.github.civcraft.zeus.rabbit;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.github.civcraft.zeus.servers.ChildServer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * This class is the glue between our application logic and RabbitMQ
 *
 */
public class RabbitGateway {

	private static RabbitGateway instance;

	public static RabbitGateway getInstance() {
		return instance;
	}

	private ConnectionFactory connectionFactory;
	private Logger logger;
	private Connection conn;
	private Map<ChildServer, Channel> incomingChannels;
	private Map<ChildServer, Channel> outgoingChannels;
	private Map<ChildServer, String> incomingChannelNames;
	private Map<ChildServer, String> outgoingChannelNames;
	private RabbitInputHandler inputHandler;

	public RabbitGateway(ConnectionFactory connFac, Map<ChildServer, String> incomingQueues,
			Map<ChildServer, String> outgoingQueues, Logger logger) {
		this.connectionFactory = connFac;
		this.incomingChannelNames = incomingQueues;
		this.outgoingChannelNames = outgoingQueues;
		this.logger = logger;
		this.inputHandler = new RabbitInputHandler(logger);
		instance = this;
	}

	public void beginAsyncListen() {
		for (ChildServer server : incomingChannelNames.keySet()) {
			new Thread(() -> {
				logger.info("Beginning to listen for rabbit input...");
				DeliverCallback deliverCallback = (consumerTag, delivery) -> {
					try {
						String message = new String(delivery.getBody(), "UTF-8");
						System.out.println(" [x] Received '" + message + "'");
						// here we just do single threaded handling per server, forwarding to a
						// threadpool would be
						// possible as well and desired in a scalable system
						inputHandler.handle(server, message);
					} catch (Exception e) {
						// if we dont do this the exception falls back into rabbit, which causes tons of
						// problems
						logger.error("Exception in rabbit listener", e);
					}
				};
				try {
					Channel incChannel = incomingChannels.get(server);
					incChannel.basicConsume(server.getID(), true, deliverCallback,
							consumerTag -> {
							});
				} catch (Exception e) {
					logger.error("Error in rabbit listener", e);
				}
			}).start();
		}
	}

	private boolean sendMessage(String server, String msg) {
		Channel chan = outgoingChannels.get(server);
		if (chan == null) {
			return false;
		}
		try {
			chan.basicPublish("", outgoingChannelNames.get(server), null, msg.getBytes("UTF-8"));
			return true;
		} catch (Exception e) {
			logger.error("Failed to send rabbit message", e);
			return false;
		}
	}

	public boolean sendMessage(ChildServer server, JSONObject json) {
		return sendMessage(server.getID(), json.toString());
	}

	public void broadcastMessage(Collection<ChildServer> servers, JSONObject json) {
		for (ChildServer server : servers) {
			//not reusing the package, because each will have a different transaction id
			sendMessage(server.getID(), json.toString());
		}
	}

	public boolean setup() {
		try {
			conn = connectionFactory.newConnection();
			for (Entry<String, String> entry : incomingChannelNames.entrySet()) {
				Channel incomingChannel = conn.createChannel();
				incomingChannel.queueDeclare(entry.getValue(), false, false, false, null);
				incomingChannels.put(entry.getKey(), incomingChannel);
			}
			for (Entry<String, String> entry : outgoingChannelNames.entrySet()) {
				Channel outgoingChannel = conn.createChannel();
				outgoingChannel.queueDeclare(entry.getValue(), false, false, false, null);
				outgoingChannels.put(entry.getKey(), outgoingChannel);
			}
			return true;
		} catch (IOException | TimeoutException e) {
			logger.error("Failed to setup rabbit connection", e);
			return false;
		}
	}

	public void shutdown() {
		try {
			for (Channel channel : incomingChannels.values()) {
				channel.close();
			}
			for (Channel channel : outgoingChannels.values()) {
				channel.close();
			}
			conn.close();
		} catch (Exception e) {
			logger.error("Failed to close rabbit connection", e);
		}
	}

}
