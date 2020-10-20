package com.github.civcraft.zeus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.civcraft.zeus.model.TransactionIdManager;
import com.github.civcraft.zeus.rabbit.BroadcastInterestTracker;

public class ZeusMain {
	
	private static ZeusMain instance;
	
	public static void main(String [] args) {
		instance = new ZeusMain();
	}
	
	public static ZeusMain getInstance() {
		return instance;
	}
	
	private Logger logger;
	private BroadcastInterestTracker broadcastInterestTracker;
	private ServerManager serverManager;
	private ServerPlacementManager serverPlacementManager;
	private TransactionIdManager transactionIdManager;
	
	private ZeusMain() {
		this.logger = LogManager.getLogger("Main");
		this.broadcastInterestTracker = new BroadcastInterestTracker();
		this.serverManager = new ServerManager();
		this.serverPlacementManager = new ServerPlacementManager();
		this.transactionIdManager = new TransactionIdManager("zeus");
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
	
	public ServerPlacementManager getServerPlacementManager() {
		return serverPlacementManager;
	}
	
	public Logger getLogger() {
		return logger;
	}

}
