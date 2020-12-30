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
	private List<ConnectedMapState> randomSpawnTargets;
	private Random rng;

	public ServerPlacementManager() {
		parts = Collections.newSetFromMap(new ConcurrentHashMap<>());
		randomSpawnTargets = new ArrayList<>();
		rng = new Random();
	}
	
	public void removeServer(ArtemisServer server) {
		Iterator<ConnectedMapState> iter = parts.iterator();
		while(iter.hasNext()) {
			ConnectedMapState next = iter.next();
			if (next.getServer().equals(server)) {
				iter.remove();
			}
		}
		synchronized (randomSpawnTargets) {
			iter = randomSpawnTargets.iterator();
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
			if (randomSpawnTargets.isEmpty()) {
				return null;
			}
			synchronized (randomSpawnTargets) {
				int index = rng.nextInt(randomSpawnTargets.size());
				return randomSpawnTargets.get(index).getServer();
			}
		}
		for (ConnectedMapState part : parts) {
			if (part.isInside(location)) {
				return part.getServer();
			}
		}
		return null;
	}

	public void registerMapPart(ConnectedMapState map) {
		Preconditions.checkNotNull(map.getServer());
		parts.add(map);
		randomSpawnTargets.remove(map);
		if (map.isRandomSpawnTarget()) {
			synchronized (randomSpawnTargets) {
				randomSpawnTargets.add(map);
			}
		}
	}

}
