package com.github.civcraft.zeus.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.github.civcraft.zeus.rabbit.PacketSession;

public class TransactionIdState {
	
	private AtomicLong localCounter;
	private Map<String, PacketSession> activeSessions;
	
	public TransactionIdState() {
		localCounter = new AtomicLong(1);
		activeSessions = new ConcurrentHashMap<>();
	}
	
	public String pullNewTicket() {
		return "zeus" + localCounter.incrementAndGet();
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
