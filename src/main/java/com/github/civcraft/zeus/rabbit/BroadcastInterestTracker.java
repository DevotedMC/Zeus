package com.github.civcraft.zeus.rabbit;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.civcraft.zeus.servers.ChildServer;

public class BroadcastInterestTracker {

	private Map<String, Set<ChildServer>> servers;

	public BroadcastInterestTracker() {
		this.servers = new HashMap<>();
	}

	Set<ChildServer> getLocalSet(String interest) {
		return servers.computeIfAbsent(interest, s -> new HashSet<>());
	}

	public void addInterest(ChildServer server, String interest) {
		getLocalSet(interest).add(server);
	}

	public boolean removeInterest(ChildServer server, String interest) {
		return getLocalSet(interest).remove(server);
	}
	
	public Set<ChildServer> getInterestedServers(String interest) {
		return Collections.unmodifiableSet(getLocalSet(interest));
	}

}
