package com.github.civcraft.zeus.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.github.civcraft.zeus.rabbit.PacketSession;

public class TransactionIdManager {
	
	private AtomicLong localCounter;
	private Map<String, PacketSession> activeSessions;
	private String identifier;
	
	public TransactionIdManager(String ownIdentifier) {
		localCounter = new AtomicLong(1);
		this.identifier = ownIdentifier;
		activeSessions = new ConcurrentHashMap<>();
	}
	
	public String pullNewTicket() {
		return identifier + localCounter.incrementAndGet();
	}
	
	public PacketSession getSession(String id) {
		return activeSessions.get(id);
	}
	
	public void deleteSession(String id) {
		activeSessions.remove(id);
	}
	
	public void putSession(String id, PacketSession session) {
		activeSessions.put(id, session);
	}

}
