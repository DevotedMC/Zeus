package com.github.civcraft.zeus;

import java.io.Console;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.civcraft.zeus.commands.ZeusCommandHandler;
import com.github.civcraft.zeus.commands.sender.ConsoleSender;
import com.github.civcraft.zeus.database.ZeusDAO;
import com.github.civcraft.zeus.model.GlobalPlayerData;
import com.github.civcraft.zeus.model.PlayerManager;
import com.github.civcraft.zeus.model.TransactionIdManager;
import com.github.civcraft.zeus.rabbit.BroadcastInterestTracker;
import com.github.civcraft.zeus.rabbit.ZeusRabbitGateway;

public class ZeusMain {

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
	private ServerPlacementManager serverPlacementManager;
	private TransactionIdManager transactionIdManager;
	private PlayerManager<GlobalPlayerData> playerDataManager;
	private ZeusCommandHandler commandHandler;
	private ZeusRabbitGateway rabbitGateway;
	private ZeusConfigManager configManager;
	private ZeusDAO dao;
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
		this.broadcastInterestTracker = new BroadcastInterestTracker();
		this.serverManager = new ServerManager();
		this.serverPlacementManager = new ServerPlacementManager();
		this.transactionIdManager = new TransactionIdManager("zeus");
		this.playerDataManager = new PlayerManager<>();
		if (!startRabbit()) {
			logger.error("Failed to start rabbit, exiting");
			System.exit(0);
		}
		this.commandHandler = new ZeusCommandHandler(logger);
		parseInput();
	}

	private boolean startRabbit() {
		rabbitGateway = new ZeusRabbitGateway(configManager.getRabbitConfig(), configManager.parseClientServers(),
				logger);
		if (!rabbitGateway.setup()) {
			return false;
		}
		rabbitGateway.beginAsyncListen();
		return true;
	}

	private void parseInput() {
		Console c = System.console();
		Scanner scanner = null;
		if (c == null) {
			logger.warn("System console not detected, using scanner as fallback behavior");
			scanner = new Scanner(System.in);
		}
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

	public ServerPlacementManager getServerPlacementManager() {
		return serverPlacementManager;
	}

	public PlayerManager<GlobalPlayerData> getPlayerDataManager() {
		return playerDataManager;
	}

	public Logger getLogger() {
		return logger;
	}

}
