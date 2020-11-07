package com.github.civcraft.zeus.model;

import java.util.UUID;

import com.github.civcraft.zeus.servers.ApolloServer;
import com.github.civcraft.zeus.servers.ArtemisServer;
import com.google.common.base.Preconditions;

public class GlobalPlayerData extends PlayerData {

	private ArtemisServer mcServer;
	private ApolloServer bungeeServer;
	private ZeusLocation intendedNextLocation;
	
	public GlobalPlayerData(UUID uuid, String name, ApolloServer bungeeServer) {
		super(uuid, name);
		Preconditions.checkNotNull(bungeeServer);
		this.bungeeServer = bungeeServer;
	}
	
	public void setIntendedNextLocation(ZeusLocation location) {
		this.intendedNextLocation = location;
	}
	
	public ZeusLocation consumeIntendedNextLocation() {
		ZeusLocation res = intendedNextLocation;
		intendedNextLocation = null;
		return res;
	}

	/**
	 * @return Bungee server the player is connected through, never null when the player is online
	 */
	public ApolloServer getBungeeServer(){
		return bungeeServer;
	}
	
	/**
	 * @return MC server the player is connected through, possibly null during transition
	 */
	public ArtemisServer getMCServer() {
		return mcServer;
	}

}
