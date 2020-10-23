package com.github.civcraft.zeus.model;

import java.util.UUID;

import com.github.civcraft.zeus.servers.ConnectedServer;
import com.google.common.base.Preconditions;

public class GlobalPlayerData extends PlayerData {

	private ConnectedServer server;
	
	public GlobalPlayerData(UUID uuid, String name, ConnectedServer server) {
		super(uuid, name);
		Preconditions.checkNotNull(server);
		this.server = server;
	}

	public ConnectedServer getServer(){
		return server;
	}

}
