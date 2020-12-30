package com.github.maxopoly.zeus.rabbit.sessions;

import java.util.UUID;

import com.github.maxopoly.zeus.rabbit.PlayerSpecificPacketSession;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class LocationRequestSession extends PlayerSpecificPacketSession {

	public LocationRequestSession(ConnectedServer source, String transactionID, UUID player) {
		super(source, transactionID, player);
	}

	@Override
	public void handleTimeout() {
		// TODO Auto-generated method stub

	}

}
