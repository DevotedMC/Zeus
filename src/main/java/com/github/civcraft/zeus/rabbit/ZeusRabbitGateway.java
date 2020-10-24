package com.github.civcraft.zeus.rabbit;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.github.civcraft.zeus.model.TransactionIdManager;
import com.github.civcraft.zeus.rabbit.abstr.AbstractRabbitInputHandler;
import com.github.civcraft.zeus.servers.ConnectedServer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * This class is the glue between our application logic and RabbitMQ
 *
 */
public class ZeusRabbitGateway {

	private static ZeusRabbitGateway instance;

	public static ZeusRabbitGateway getInstance() {
		return instance;
	}
	
	public static String getChannelToZeus(String clientName) {
		return clientName + "_up";
	}
	
	public static String getChannelFromZeus(String clientName) {
		return clientName + "_down";
	}

	private ConnectionFactory connectionFactory;
	private Logger logger;
	private Connection conn;
	private Map<ConnectedServer, Channel> incomingChannels;
	private Map<ConnectedServer, Channel> outgoingChannels;
	private List<ConnectedServer> connectedServers;
	private AbstractRabbitInputHandler inputHandler;

	public ZeusRabbitGateway(ConnectionFactory connFac, List <ConnectedServer> connectedServers, Logger logger) {
		this.connectionFactory = connFac;
		this.logger = logger;
		this.connectedServers = connectedServers;
		this.inputHandler = new ZeusRabbitInputHandler(new TransactionIdManager("zeus"), logger);
		this.incomingChannels = new HashMap<>();
		this.outgoingChannels = new HashMap<>();
		instance = this;
	}

	public void beginAsyncListen() {
		for (ConnectedServer server : connectedServers) {
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
					incChannel.basicConsume(getChannelToZeus(server.getID()), true, deliverCallback,
							consumerTag -> {
							});
				} catch (Exception e) {
					logger.error("Error in rabbit listener", e);
				}
			}).start();
		}
	}

	private boolean sendMessage(ConnectedServer server, String msg) {
		Channel chan = outgoingChannels.get(server);
		if (chan == null) {
			return false;
		}
		try {
			chan.basicPublish("", getChannelFromZeus(server.getID()), null, msg.getBytes("UTF-8"));
			return true;
		} catch (Exception e) {
			logger.error("Failed to send rabbit message", e);
			return false;
		}
	}

	public boolean sendMessage(ConnectedServer server, JSONObject json) {
		return sendMessage(server, json.toString());
	}

	public void broadcastMessage(Collection<ConnectedServer> servers, JSONObject json) {
		for (ConnectedServer server : servers) {
			//not reusing the package, because each will have a different transaction id
			sendMessage(server, json.toString());
		}
	}

	public boolean setup() {
		try {
			conn = connectionFactory.newConnection();
			for (ConnectedServer server : connectedServers) {
				Channel incomingChannel = conn.createChannel();
				incomingChannel.queueDeclare(getChannelToZeus(server.getID()), false, false, false, null);
				incomingChannels.put(server, incomingChannel);
			}
			for (ConnectedServer server : connectedServers) {
				Channel outgoingChannel = conn.createChannel();
				outgoingChannel.queueDeclare(getChannelFromZeus(server.getID()), false, false, false, null);
				outgoingChannels.put(server, outgoingChannel);
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
