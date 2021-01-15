package com.github.maxopoly.zeus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.maxopoly.zeus.model.ConnectedMapState;
import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.servers.ArtemisServer;
import com.google.common.base.Preconditions;

public class ServerPlacementManager {

	private Set<ConnectedMapState> parts; // TODO O(log(n)) instead of O(n)
	private List<ConnectedMapState> firstSpawnTargets;
	private Random rng;

	public ServerPlacementManager() {
		parts = Collections.newSetFromMap(new ConcurrentHashMap<>());
		firstSpawnTargets = new ArrayList<>();
		rng = new Random();
	}
	
	public synchronized void removeServer(ArtemisServer server) {
		Iterator<ConnectedMapState> iter = parts.iterator();
		while(iter.hasNext()) {
			ConnectedMapState next = iter.next();
			if (next.getServer().equals(server)) {
				iter.remove();
			}
		}
		synchronized (firstSpawnTargets) {
			iter = firstSpawnTargets.iterator();
			while(iter.hasNext()) {
				ConnectedMapState next = iter.next();
				if (next.getServer().equals(server)) {
					iter.remove();
				}
			}
		}
	}

	public ArtemisServer getTargetServer(ArtemisServer source, ZeusLocation location) {
		for (ConnectedMapState part : parts) {
			if (part.equals(source)) {
				continue;
			}
			if (part.isInside(location)) {
				return part.getServer();
			}
		}
		return null;
	}

	public ArtemisServer getTargetServer(ZeusLocation location) {
		if (location == null) {
			if (firstSpawnTargets.isEmpty()) {
				return null;
			}
			synchronized (firstSpawnTargets) {
				int index = rng.nextInt(firstSpawnTargets.size());
				return firstSpawnTargets.get(index).getServer();
			}
		}
		for (ConnectedMapState part : parts) {
			if (part.isInside(location)) {
				return part.getServer();
			}
		}
		return null;
	}

	public synchronized void registerMapPart(ConnectedMapState map) {
		Preconditions.checkNotNull(map.getServer());
		parts.add(map);
		firstSpawnTargets.remove(map);
		if (map.isFirstSpawnTarget()) {
			synchronized (firstSpawnTargets) {
				firstSpawnTargets.add(map);
			}
		}
	}

}
