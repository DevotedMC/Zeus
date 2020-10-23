package com.github.civcraft.zeus.model;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager<T extends PlayerData> {
	
	private Map<String, T> playersByName;
	private Map<UUID, T> playersByUUID;
	
	public PlayerManager() {
		this.playersByName = new ConcurrentHashMap<>();
		this.playersByUUID = new ConcurrentHashMap<>();
	}
	
	public T getLoggedInPlayerByName(String name) {
		return playersByName.get(name.toLowerCase());
	}
	
	public T getLoggedInPlayerByUUID(UUID uuid) {
		return playersByUUID.get(uuid);
	}
	
	public void addPlayer(T data) {
		playersByName.put(data.getName().toLowerCase(), data);
		playersByUUID.put(data.getUUID(), data);
	}
	
	public void removePlayer(T data) {
		playersByName.remove(data.getName().toLowerCase());
		playersByUUID.remove(data.getUUID());
	}

}
