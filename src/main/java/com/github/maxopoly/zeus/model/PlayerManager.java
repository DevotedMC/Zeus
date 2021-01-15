package com.github.maxopoly.zeus.model;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager<T extends PlayerData> {

	protected final Map<String, UUID> cacheNameToUUID;
	protected final Map<UUID, String> cacheUUIDToName;
	protected final Map<UUID, T> playersByUUID;

	public PlayerManager() {
		this.cacheNameToUUID = new ConcurrentHashMap<>();
		this.cacheUUIDToName = new ConcurrentHashMap<>();
		this.playersByUUID = new ConcurrentHashMap<>();
	}
	
	public UUID getUUID(String name) {
		return cacheNameToUUID.get(name.toLowerCase());
	}
	
	public String getName(UUID uuid) {
		return cacheUUIDToName.get(uuid);
	}

	public T getOnlinePlayerData(String name) {
		UUID uuid = getUUID(name);
		if (uuid == null) {
			return null;
		}
		return playersByUUID.get(uuid);
	}

	public T getOnlinePlayerData(UUID uuid) {
		return playersByUUID.get(uuid);
	}

	public void addPlayer(T data) {
		playersByUUID.put(data.getUUID(), data);
		cacheNameToUUID.put(data.getName().toLowerCase(), data.getUUID());
		cacheUUIDToName.put(data.getUUID(), data.getName());
	}
	
	public void addToNameUUIDCache(UUID uuid, String name) {
		cacheNameToUUID.put(name.toLowerCase(), uuid);
		cacheUUIDToName.put(uuid, name);
	}

	public void removeOnlinePlayerData(UUID uuid) {
		playersByUUID.remove(uuid);
	}
}
