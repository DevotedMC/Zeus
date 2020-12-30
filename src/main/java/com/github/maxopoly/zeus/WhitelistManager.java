package com.github.maxopoly.zeus;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.maxopoly.zeus.database.ZeusDAO;
import com.github.maxopoly.zeus.servers.ArtemisServer;
import com.google.common.base.Preconditions;

public class WhitelistManager {

	private Map<ArtemisServer, Integer> localWhitelistLevels;
	private int globalWhitelistLevel;
	private ZeusDAO dao;

	public WhitelistManager(ZeusDAO dao, int globalWhitelistLevel) {
		this.localWhitelistLevels = new HashMap<>();
		this.globalWhitelistLevel = globalWhitelistLevel;
		this.dao = dao;
	}

	public int getWhiteListLevelServer(ArtemisServer server) {
		return localWhitelistLevels.getOrDefault(server, 0);
	}

	public int getWhitelistLevelPlayer(UUID uuid) {
		return dao.getWhitelistLevel(uuid);
	}

	public void setWhitelistLevelPlayer(UUID uuid, int level) {
		Preconditions.checkNotNull(uuid);
		dao.setWhitelistLevel(uuid, level);
	}

	public void setWhitelistLevelServer(ArtemisServer server, int level) {
		Preconditions.checkNotNull(server);
		Preconditions.checkArgument(level >= 0);
		localWhitelistLevels.put(server, level);
	}

	public void setGlobalWhitelistLevel(int level) {
		Preconditions.checkArgument(level >= 0);
		this.globalWhitelistLevel = level;
	}

	public int getGlobalWhitelistLevel() {
		return globalWhitelistLevel;
	}

	public boolean canEnterServer(UUID uuid, ArtemisServer server) {
		return getWhitelistLevelPlayer(uuid) >= Math.max(getWhiteListLevelServer(server), globalWhitelistLevel);
	}

}
