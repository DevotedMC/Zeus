package com.github.maxopoly.zeus.plugin.event.events;

import java.util.UUID;

import com.github.maxopoly.zeus.plugin.event.ZeusEvent;
import com.github.maxopoly.zeus.servers.ArtemisServer;

/**
 * Called when a server is about to be sent a players player data
 *
 */
public class ServerLoadPlayerDataEvent implements ZeusEvent {

	private UUID player;
	private ArtemisServer server;
	
	public ServerLoadPlayerDataEvent(UUID player, ArtemisServer server) {
		super();
		this.player = player;
		this.server = server;
	}
	
	public UUID getPlayer() {
		return player;
	}
	public ArtemisServer getServer() {
		return server;
	}
	
}
