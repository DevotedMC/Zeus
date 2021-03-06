package com.github.maxopoly.zeus.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import com.github.maxopoly.zeus.rabbit.PacketSession;

public class TransactionIdManager {

	private AtomicLong localCounter;
	private Map<String, PacketSession> activeSessions;
	private TreeSet<PacketSession> timeoutTracker;
	private String identifier;
	private Consumer<String> infoLogger;

	public TransactionIdManager(String ownIdentifier, Consumer<String> infoLogger) {
		localCounter = new AtomicLong(1);
		this.identifier = ownIdentifier;
		this.infoLogger = infoLogger;
		activeSessions = new ConcurrentHashMap<>();
		this.timeoutTracker = new TreeSet<>((p1, p2) -> {
			return Long.compare(p1.getExpirationTimestamp(), p2.getExpirationTimestamp());
		});
	}

	public void printActiveSessions(Consumer<String> logger) {
		for (Entry<String, PacketSession> entry : activeSessions.entrySet()) {
			PacketSession ses = entry.getValue();
			logger.accept(String.format("Session %s with server %s, created: %d,  expires: %d,  left duration: %d",
					ses.getTransactionID(), ses.getServerTalkedTo().getID(), ses.getCreationTime(),
					ses.getExpirationTimestamp(), ses.getExpirationTimestamp() - System.currentTimeMillis()));
		}
	}

	public boolean hasActiveSessions() {
		return !activeSessions.isEmpty();
	}

	public void forceCleanupSession() {
		Set<String> keys = new HashSet<>(activeSessions.keySet());
		for(String key : keys) {
			PacketSession session = activeSessions.get(key);
			if (session != null && session.hasExpired()) {
				activeSessions.remove(key);
			}
		}
	}

	public void updateTimeouts() {
		synchronized (timeoutTracker) {
			Iterator<PacketSession> iter = timeoutTracker.iterator();
			while (iter.hasNext()) {
				PacketSession session = iter.next();
				if (session.hasExpired()) {
					infoLogger.accept(
							String.format("Session %s expired at %d ms", session, session.getExpirationTimestamp()));
					session.handleTimeout();
					iter.remove();
					activeSessions.remove(session.getTransactionID());
				} else {
					return;
				}
			}
		}
	}

	public String pullNewTicket() {
		return identifier + "_" + localCounter.incrementAndGet();
	}

	public PacketSession getSession(String id) {
		return activeSessions.get(id);
	}

	public void deleteSession(String id) {
		PacketSession session = activeSessions.remove(id);
		if (session != null) {
			synchronized (timeoutTracker) {
				timeoutTracker.remove(session);
			}
		}
	}

	public void putSession(PacketSession session) {
		activeSessions.put(session.getTransactionID(), session);
		synchronized (timeoutTracker) {
			timeoutTracker.add(session);
		}
		session.setExpirationUpdate(l -> {
			synchronized (timeoutTracker) {
				timeoutTracker.remove(session);
				session.updateInternalExpirationTimer(l);
				timeoutTracker.add(session);
			}
		});
	}

}
