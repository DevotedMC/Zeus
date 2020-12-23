package com.github.civcraft.zeus;

import java.util.UUID;

import com.github.civcraft.zeus.model.GlobalPlayerData;
import com.github.civcraft.zeus.model.PlayerManager;

public class ZeusPlayerManager extends PlayerManager<GlobalPlayerData> {

	public String getOfflinePlayerName(UUID playerUUID) {
		return null;
	}

	public UUID getOfflinePlayerUUID(String name) {
		return null;
	}

}
