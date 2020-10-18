package com.github.civcraft.zeus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.github.civcraft.zeus.rabbit.PacketSession;

public class TransactionIdState {
	
	private AtomicLong serverSideCounter;
	private Map<Long, PacketSession> activeSessions;
	
	public TransactionIdState() {
		serverSideCounter = new AtomicLong(1);
		activeSessions = new ConcurrentHashMap<>();
	}
	
	public long pullNewTicket() {
		return serverSideCounter.incrementAndGet();
	}
	
	public PacketSession getSession(long id) {
		return activeSessions.get(id);
	}
	
	public void deleteSession(long id) {
		activeSessions.remove(id);
	}
	
	public void putSession(long id, PacketSession session) {
		activeSessions.put(id, session);
	}

}
