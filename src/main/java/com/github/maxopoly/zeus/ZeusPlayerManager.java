package com.github.maxopoly.zeus;

import java.util.UUID;

import com.github.maxopoly.zeus.database.ZeusDAO;
import com.github.maxopoly.zeus.model.GlobalPlayerData;
import com.github.maxopoly.zeus.model.PlayerManager;

public class ZeusPlayerManager extends PlayerManager<GlobalPlayerData> {

	private ZeusDAO dao;
	
	public ZeusPlayerManager(ZeusDAO dao) {
		this.dao = dao;
	}
	
	public String getName(UUID playerUUID) {
		String name = super.getName(playerUUID);
		if (name != null) {
			return name;
		}
		name = dao.getPlayerName(playerUUID);
		if (name == null) {
			return null;
		}
		cacheNameToUUID.put(name.toLowerCase(), playerUUID);
		cacheUUIDToName.put(playerUUID, name);
		return name;
	}

	public UUID getUUID(String name) {
		UUID uuid = super.getUUID(name);
		if (uuid != null) {
			return uuid;
		}
		uuid = dao.getPlayerUUID(name);
		if (uuid == null) {
			return null;
		}
		//dont fill other direction of cache because upper/lower case of passed argument might be wrong
		cacheNameToUUID.put(name.toLowerCase(), uuid);
		return uuid;
	}

}
