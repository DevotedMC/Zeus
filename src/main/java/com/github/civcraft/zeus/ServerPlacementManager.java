package com.github.civcraft.zeus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.civcraft.zeus.model.ConnectedMapState;
import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.servers.ArtemisServer;
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
			synchronized (rng) {
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
			randomSpawnTargets.add(map);
		}
	}

}
