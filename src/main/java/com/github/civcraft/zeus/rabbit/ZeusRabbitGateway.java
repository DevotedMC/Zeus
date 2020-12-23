package com.github.civcraft.zeus.rabbit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.model.TransactionIdManager;
import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.servers.ConnectedServer;
import com.google.common.base.Charsets;
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
	private ZeusRabbitInputHandler inputHandler;

	public ZeusRabbitGateway(ConnectionFactory connFac, Collection<ConnectedServer> connectedServers, Logger logger) {
		this.connectionFactory = connFac;
		this.logger = logger;
		this.connectedServers = new ArrayList<>(connectedServers);
		this.inputHandler = new ZeusRabbitInputHandler(new TransactionIdManager("zeus", logger::info), logger);
		this.incomingChannels = new HashMap<>();
		this.outgoingChannels = new HashMap<>();
		instance = this;
	}

	public void beginAsyncListen() {
		for (ConnectedServer server : connectedServers) {
			new Thread(() -> {
				logger.info("Beginning to listen for rabbit input from " + server.getID());
				DeliverCallback deliverCallback = (consumerTag, delivery) -> {
					try {
						String message = new String(delivery.getBody(), "UTF-8");
						if (ZeusMain.getInstance().getConfigManager().debugRabbit()) {
							logger.info("[X] R_IN [" + server.getID() + "]: " + message);
						}
						// here we just do single threaded handling per server, forwarding to a
						// threadpool would be
						// possible as well and maybe desired in a scalable system
						inputHandler.handle(server, message);
					} catch (Exception e) {
						// if we dont do this the exception falls back into rabbit, which causes tons of
						// problems
						logger.error("Exception in rabbit listener", e);
					}
				};
				try {
					Channel incChannel = incomingChannels.get(server);
					incChannel.basicConsume(getChannelToZeus(server.getID()), true, deliverCallback, consumerTag -> {
					});
				} catch (Exception e) {
					logger.error("Error in rabbit listener", e);
				}
			}).start();
		}
	}

	public ZeusRabbitInputHandler getInputHandler() {
		return inputHandler;
	}

	private boolean sendMessage(ConnectedServer server, String msg) {
		Channel chan = outgoingChannels.get(server);
		if (chan == null) {
			return false;
		}
		try {
			if (ZeusMain.getInstance().getConfigManager().debugRabbit()) {
				logger.info(String.format("[X] R_OUT [%s]: %s", server.getID(), msg));
			}
			chan.basicPublish("", getChannelFromZeus(server.getID()), null, msg.getBytes(Charsets.UTF_8));
			return true;
		} catch (Exception e) {
			logger.error("Failed to send rabbit message", e);
			return false;
		}
	}

	public boolean sendMessage(ConnectedServer server, JSONObject json) {
		return sendMessage(server, json.toString());
	}

	public boolean sendMessage(ConnectedServer server, RabbitMessage msg) {
		return sendMessage(server, msg.getJSON());
	}

	public void broadcastMessage(Collection<ConnectedServer> servers, JSONObject json) {
		for (ConnectedServer server : servers) {
			// not reusing the package, because each will have a different transaction id
			sendMessage(server, json.toString());
		}
	}

	public void broadcastToAll(JSONObject json) {
		for (ConnectedServer server : this.connectedServers) {
			sendMessage(server, json.toString());
		}
	}

	public boolean setup() {
		InteractiveRabbitCommand.setSendingLambda((s, p) -> sendMessage(s, p.getJSON()));
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
