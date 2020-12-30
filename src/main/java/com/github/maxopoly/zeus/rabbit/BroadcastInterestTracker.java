package com.github.maxopoly.zeus.rabbit;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.maxopoly.zeus.servers.ConnectedServer;

public class BroadcastInterestTracker {

	private Map<String, Set<ConnectedServer>> servers;

	public BroadcastInterestTracker() {
		this.servers = new HashMap<>();
	}

	private Set<ConnectedServer> getLocalSet(String interest) {
		return servers.computeIfAbsent(interest, s -> new HashSet<>());
	}

	public synchronized void addInterest(ConnectedServer server, String interest) {
		getLocalSet(interest).add(server);
	}

	public synchronized boolean removeInterest(ConnectedServer server, String interest) {
		return getLocalSet(interest).remove(server);
	}

	public synchronized Set<ConnectedServer> getInterestedServers(String interest) {
		return Collections.unmodifiableSet(getLocalSet(interest));
	}

	public synchronized void broadcastMessage(RabbitMessage message) {
		ZeusRabbitGateway.getInstance().broadcastMessage(getInterestedServers(message.getIdentifier()),
				message.getJSON());
	}

}
