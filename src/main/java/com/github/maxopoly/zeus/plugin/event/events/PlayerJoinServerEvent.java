package com.github.maxopoly.zeus.plugin.event.events;

import com.github.maxopoly.zeus.model.GlobalPlayerData;
import com.github.maxopoly.zeus.plugin.event.ZeusEvent;
import com.github.maxopoly.zeus.servers.ArtemisServer;

/**
 * Called after an Artemis server has accepted a player join and before the
 * player data is sent to the server.
 *
 */
public class PlayerJoinServerEvent implements ZeusEvent {

	private GlobalPlayerData playerData;
	private ArtemisServer previousServer;
	private ArtemisServer newServer;

	public PlayerJoinServerEvent(GlobalPlayerData playerData, ArtemisServer previousServer, ArtemisServer newServer) {
		this.playerData = playerData;
		this.previousServer = previousServer;
		this.newServer = newServer;
	}

	public GlobalPlayerData getPlayerData() {
		return playerData;
	}

	/**
	 * Gets the server the player was on previously, may be null if the player is
	 * only just joining the network
	 * 
	 * @return Server the player was on before
	 */
	public ArtemisServer getPreviousServer() {
		return previousServer;
	}
	
	/**
	 * @return Server the player is joining
	 */
	public ArtemisServer getNewServer() {
		return newServer;
	}

}
