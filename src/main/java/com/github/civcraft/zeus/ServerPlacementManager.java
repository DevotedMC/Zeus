package com.github.civcraft.zeus;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.civcraft.zeus.model.ConnectedMapState;
import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.servers.ArtemisServer;
import com.google.common.base.Preconditions;

public class ServerPlacementManager {
	
	private Set<ConnectedMapState> parts; //TODO O(log(n)) instead of O(n)
	
	public ServerPlacementManager() {
		parts = Collections.newSetFromMap(new ConcurrentHashMap<>());
	}
	
	public ArtemisServer getTargetServer(ArtemisServer source, ZeusLocation location) {
		for(ConnectedMapState part : parts) {
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
		for(ConnectedMapState part : parts) {
			if (part.isInside(location)) {
				return part.getServer();
			}
		}
		return null;
	}
	
	public void registerMapPart(ConnectedMapState map) {
		Preconditions.checkNotNull(map.getServer());
		parts.add(map);
	}

}
