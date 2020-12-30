package com.github.maxopoly.zeus.plugin.event.events;

import com.github.maxopoly.zeus.model.GlobalPlayerData;
import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.plugin.event.CancellableEvent;
import com.github.maxopoly.zeus.servers.ArtemisServer;

/**
 * Called when a player transfer from one server to another is attempted.
 * Cancelling this transfer will return the player to their source server
 *
 */
public class RequestPlayerTransferEvent extends CancellableEvent {

	private ArtemisServer sourceServer;
	private ArtemisServer targetServer;
	private GlobalPlayerData player;
	private ZeusLocation location;

	public RequestPlayerTransferEvent(ArtemisServer sourceServer, ZeusLocation location, ArtemisServer targetServer,
			GlobalPlayerData data) {
		this.sourceServer = sourceServer;
		this.targetServer = targetServer;
		this.player = data;
		this.location = location;
	}

	/**
	 * @return Player being transferred
	 */
	public GlobalPlayerData getPlayer() {
		return player;
	}

	/**
	 * @return Server the player was on before. May be the source of the request,
	 *         but must not neccesarily, for example if a teleport was requested
	 *         from something else
	 */
	public ArtemisServer getSourceServer() {
		return sourceServer;
	}

	/**
	 * @return Server the player will be sent to
	 */
	public ArtemisServer getTargetServer() {
		return targetServer;
	}

	/**
	 * @return Location the player will be at on the target server
	 */
	public ZeusLocation getLocation() {
		return location;
	}

}
