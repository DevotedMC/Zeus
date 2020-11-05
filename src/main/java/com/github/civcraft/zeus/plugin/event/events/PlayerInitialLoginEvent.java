package com.github.civcraft.zeus.plugin.event.events;

import java.net.InetAddress;
import java.util.UUID;

import com.github.civcraft.zeus.plugin.event.CancellableEvent;

/**
 * Called when a player is first connecting to a Bungee server from outside
 *
 */
public class PlayerInitialLoginEvent extends CancellableEvent {

	private UUID player;
	private InetAddress ip;
	private String denyMessage;

	public PlayerInitialLoginEvent(UUID player, InetAddress ip) {
		this.player = player;
		this.ip = ip;
	}

	/**
	 * @return Player attempting to login
	 */
	public UUID getPlayer() {
		return player;
	}

	/**
	 * @return IP the player is connecting from, can be either v4 or v6
	 */
	public InetAddress getPlayerIP() {
		return ip;
	}

	/**
	 * @return Message to show the player if his login is denied
	 */
	public String getDenyMessage() {
		return denyMessage;
	}

	/**
	 * Sets the message shown to the player if his login is denied and sets the
	 * login attempt to be cancelled
	 * 
	 * @param denyMessage Message to show
	 */
	public void setDenyMessage(String denyMessage) {
		this.denyMessage = denyMessage;
		setCancelled(true);
	}

}
