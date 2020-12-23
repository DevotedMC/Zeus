package com.github.civcraft.zeus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.civcraft.zeus.servers.ConnectedServer;

public class ServerManager {

	private Map<String, ConnectedServer> servers;
	private Map<Class<? extends ConnectedServer>, Set<ConnectedServer>> serversByType;

	public ServerManager(Collection<ConnectedServer> servers) {
		this.servers = new HashMap<>();
		this.serversByType = new HashMap<>();
		for (ConnectedServer server : servers) {
			registerServer(server);
		}
	}

	public void registerServer(ConnectedServer server) {
		servers.put(server.getID(), server);
		serversByType.computeIfAbsent(server.getClass(), c -> new HashSet<>()).add(server);
	}

	public void unregisterServer(ConnectedServer server) {
		servers.remove(server.getID());
		serversByType.computeIfAbsent(server.getClass(), c -> new HashSet<>()).remove(server);
	}

	public ConnectedServer getServer(String identifier) {
		return null;
	}

	public Collection<ConnectedServer> getAllServer() {
		return new ArrayList<>(servers.values());
	}

}
