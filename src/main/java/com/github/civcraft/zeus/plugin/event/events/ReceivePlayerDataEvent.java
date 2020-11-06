package com.github.civcraft.zeus.plugin.event.events;

import java.util.UUID;

import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.plugin.event.ZeusEvent;
import com.github.civcraft.zeus.servers.ArtemisServer;

public class ReceivePlayerDataEvent implements ZeusEvent {
    
	private UUID player;
	private ZeusLocation location;
	private byte[] data;
	private ArtemisServer sendingServer;
	
	
	
	public ReceivePlayerDataEvent(UUID player, byte[] data, ZeusLocation location,  ArtemisServer sendingServer) {
		this.player = player;
		this.location = location;
		this.data = data;
		this.sendingServer = sendingServer;
	}
	
	/**
	 * @return UUID of the player whose data is being written
	 */
	public UUID getPlayer() {
		return player;
	}
	
	/**
	 * @return Location stored for the player
	 */
	public ZeusLocation getLocation() {
		return location;
	}
	
	/**
	 * @return Serialized NBT of the player
	 */
	public byte[] getPlayerData() {
		return data;
	}
	
	/**
	 * @return Server the player is being sent from
     */
	public ArtemisServer getSendingServer() {
		return sendingServer;
	}
	
}