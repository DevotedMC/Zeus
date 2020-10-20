package com.github.civcraft.zeus.rabbit;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.civcraft.zeus.servers.ConnectedServer;

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
	
	public synchronized void broadcastMessage(String interest, RabbitMessage message) {
		RabbitGateway.getInstance().broadcastMessage(getInterestedServers(interest), message.getJSON());
	}

}
