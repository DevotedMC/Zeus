package com.github.civcraft.zeus.rabbit.sessions;

import java.util.UUID;

import com.github.civcraft.zeus.rabbit.PacketSession;
import com.github.civcraft.zeus.servers.ArtemisServer;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class LocationRequestSession extends PacketSession {

	private UUID player;
	
	public LocationRequestSession(ConnectedServer source, String transactionID, UUID player) {
		super(source, transactionID);
		this.player = player;
	}
	
	public UUID getPlayer() {
		return player;
	}

	@Override
	public void handleTimeout() {
		// TODO Auto-generated method stub
		
	}

}
