package com.github.civcraft.zeus;

import java.io.Console;
import java.io.File;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.civcraft.zeus.commands.ZeusCommandHandler;
import com.github.civcraft.zeus.commands.sender.ConsoleSender;
import com.github.civcraft.zeus.database.ZeusDAO;
import com.github.civcraft.zeus.model.TransactionIdManager;
import com.github.civcraft.zeus.plugin.ZeusPluginManager;
import com.github.civcraft.zeus.plugin.event.EventBroadcaster;
import com.github.civcraft.zeus.rabbit.BroadcastInterestTracker;
import com.github.civcraft.zeus.rabbit.ZeusRabbitGateway;
import com.github.civcraft.zeus.rabbit.outgoing.ResetConnectionPacket;

public final class ZeusMain {

	private static ZeusMain instance;

	public static void main(String[] args) {
		new ZeusMain();
	}

	public static ZeusMain getInstance() {
		return instance;
	}

	private Logger logger;
	private BroadcastInterestTracker broadcastInterestTracker;
	private ServerManager serverManager;
	private ZeusPlayerManager playerManager;
	private ServerPlacementManager serverPlacementManager;
	private TransactionIdManager transactionIdManager;
	private ZeusCommandHandler commandHandler;
	private ZeusRabbitGateway rabbitGateway;
	private ZeusConfigManager configManager;
	private ZeusDAO dao;
	private ZeusPluginManager pluginManager;
	private EventBroadcaster eventManager;
	 private ScheduledExecutorService transactionCleanupThread;
	private boolean isRunning = true;

	private ZeusMain() {
		instance = this;
		this.logger = LogManager.getLogger(getClass());
		logger.info("Launching Zeus...");
		this.configManager = new ZeusConfigManager(logger);
		if (!configManager.reload()) {
			logger.error("Failed to load config, exiting");
			System.exit(0);
		}
		this.dao = new ZeusDAO(configManager.getDatabase(), logger);
		if (!dao.createTables()) {
			logger.error("Failed to init DB, shutting down");
			System.exit(0);
		}
		this.playerManager = new ZeusPlayerManager();
		this.eventManager = new EventBroadcaster(logger);
		this.pluginManager = new ZeusPluginManager(logger, new File("."));
		this.broadcastInterestTracker = new BroadcastInterestTracker();
		this.serverManager = new ServerManager(configManager.parseClientServers());
		this.serverPlacementManager = new ServerPlacementManager();
		this.transactionIdManager = new TransactionIdManager("zeus", logger::info);
		if (!startRabbit()) {
			logger.error("Failed to start rabbit, exiting");
			System.exit(0);
		}
		this.commandHandler = new ZeusCommandHandler(logger);
		this.pluginManager.enableAllPlugins();
		transactionCleanupThread = Executors.newScheduledThreadPool(1);
		transactionCleanupThread.scheduleAtFixedRate(transactionIdManager::updateTimeouts, 10, 10, TimeUnit.MILLISECONDS);
		parseInput();
	}

	private boolean startRabbit() {
		rabbitGateway = new ZeusRabbitGateway(configManager.getRabbitConfig(), serverManager.getAllServer(),
				logger);
		if (!rabbitGateway.setup()) {
			return false;
		}
		rabbitGateway.beginAsyncListen();
		rabbitGateway.broadcastToAll(new ResetConnectionPacket(transactionIdManager.pullNewTicket()).getJSON());
		return true;
	}

	private void parseInput() {
		Console c = System.console();
		Scanner scanner = null;
		if (c == null) {
			logger.warn("System console not detected, using scanner as fallback behavior");
			scanner = new Scanner(System.in);
		}
		logger.info("Started listening for console input");
		while (isRunning) {
			String msg;
			if (c == null) {
				msg = scanner.nextLine();
			} else {
				msg = c.readLine("");
			}
			if (msg == null) {
				continue;
			}
			commandHandler.handleInput(new ConsoleSender(), msg);
		}
	}

	public void shutDown() {
		rabbitGateway.shutdown();
		isRunning = false;
	}

	public ServerManager getServerManager() {
		return serverManager;
	}

	public TransactionIdManager getTransactionIdManager() {
		return transactionIdManager;
	}

	public BroadcastInterestTracker getBroadcastInterestTracker() {
		return broadcastInterestTracker;
	}

	public ZeusRabbitGateway getRabbitGateway() {
		return rabbitGateway;
	}
	
	public ZeusDAO getDAO() {
		return dao;
	}
	
	public ZeusPlayerManager getPlayerManager() {
		return playerManager;
	}
	
	public ZeusPluginManager getPluginManager() {
		return pluginManager;
	}
	
	public EventBroadcaster getEventManager() {
		return eventManager;
	}

	public ServerPlacementManager getServerPlacementManager() {
		return serverPlacementManager;
	}
	
	public ZeusConfigManager getConfigManager() {
		return configManager;
	}

	public Logger getLogger() {
		return logger;
	}

}
