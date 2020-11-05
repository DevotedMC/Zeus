package com.github.civcraft.zeus.plugin.event.events;

import java.util.UUID;

import com.github.civcraft.zeus.plugin.event.ZeusEvent;

/**
 * Called after a player has fully disconnected from the bungee instance he was connected to. Assume his connection to already be closed at this point
 *
 */
public class PlayerFinalDisconnectEvent implements ZeusEvent {

	private UUID player;
	
	public PlayerFinalDisconnectEvent(UUID player) {
		this.player = player;
	}
	
	/**
	 * @return Player who disconnected
	 */
	public UUID getPlayer() {
		return player;
	}
	
}
