package com.github.civcraft.zeus.rabbit.sessions;

import java.util.UUID;

import com.github.civcraft.zeus.rabbit.PlayerSpecificPacketSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class PlayerLoginSession extends PlayerSpecificPacketSession {

	public PlayerLoginSession(ConnectedServer source, String transactionID, UUID player) {
		super(source, transactionID, player);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleTimeout() {
		// TODO Auto-generated method stub
		
	}

}
