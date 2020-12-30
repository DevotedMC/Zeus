package com.github.maxopoly.zeus.rabbit;

import java.util.UUID;

import com.github.maxopoly.zeus.servers.ConnectedServer;

public abstract class PlayerSpecificPacketSession extends PacketSession {

	private UUID player;

	public PlayerSpecificPacketSession(ConnectedServer source, String transactionID, UUID player) {
		super(source, transactionID);
		this.player = player;
	}

	public UUID getPlayer() {
		return player;
	}
}
