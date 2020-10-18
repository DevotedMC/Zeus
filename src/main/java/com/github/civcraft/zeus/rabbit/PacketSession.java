package com.github.civcraft.zeus.rabbit;

import com.github.civcraft.zeus.servers.ChildServer;

/**
 * Uniquely identifies a single conversation between server and client
 *
 */
public abstract class PacketSession {
	
	private final ChildServer source;
	private final String transactionID;
	private final long creationTime;
	private long lastUpdate;
	
	public PacketSession(ChildServer source, String transactionID) {
		this.source = source;
		this.transactionID = transactionID;
		this.creationTime = System.currentTimeMillis();
		this.lastUpdate = creationTime;
	}
	
	public void refreshUpdateTimer() {
		this.lastUpdate = System.currentTimeMillis();
	}
	
	public boolean hasExpired() {
		return System.currentTimeMillis() - lastUpdate > getExpirationTime();
	}
	
	protected long getExpirationTime() {
		return 10_000L;
	}
	
	public String getTransactionID() {
		return transactionID;
	}
	
	public long getCreationTime() {
		return creationTime;
	}
	
	public ChildServer getServerID() {
		return this.source;
	}
	
	public abstract void handleTimeout();

}
