package com.github.civcraft.zeus.rabbit;

import java.util.function.LongConsumer;

import com.github.civcraft.zeus.servers.ConnectedServer;

/**
 * Uniquely identifies a single conversation between server and client
 *
 */
public abstract class PacketSession {
	
	private final ConnectedServer source;
	private final String transactionID;
	private final long creationTime;
	private long lastUpdate;
	private LongConsumer expirationHandler;
	
	public PacketSession(ConnectedServer source, String transactionID) {
		this.source = source;
		this.transactionID = transactionID;
		this.creationTime = System.currentTimeMillis();
		this.lastUpdate = creationTime;
	}
	
	public synchronized void refreshUpdateTimer() {
		long newUpdate = System.currentTimeMillis();
		expirationHandler.accept(newUpdate);
	}
	
	/**
	 * Don't use this from anywhere but the transaction id manager
	 * @param time Updated timestamp
	 */
	public void updateInternalExpirationTimer(long time) {
		this.lastUpdate = time;
	}
	
	public boolean hasExpired() {
		return System.currentTimeMillis() - lastUpdate > getExpirationTime();
	}
	
	protected long getExpirationTime() {
		return 10_000L;
	}
	
	public long getExpirationTimestamp() {
		return lastUpdate + getExpirationTime();
	}
	
	public String getTransactionID() {
		return transactionID;
	}
	
	public long getCreationTime() {
		return creationTime;
	}
	
	public ConnectedServer getServerID() {
		return this.source;
	}
	
	public void setExpirationUpdate(LongConsumer expirationHandler) {
		this.expirationHandler = expirationHandler;
	}
	
	public abstract void handleTimeout();

}
