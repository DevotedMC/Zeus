package com.github.civcraft.zeus.model;

import java.util.UUID;

import com.github.civcraft.zeus.servers.ConnectedServer;
import com.google.common.base.Preconditions;

public class PlayerData {
	
	protected UUID uuid;
	protected String name;
	
	public PlayerData(UUID uuid, String name) {
		Preconditions.checkNotNull(uuid);
		Preconditions.checkNotNull(name);
		this.uuid = uuid;
		this.name = name;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}

}
