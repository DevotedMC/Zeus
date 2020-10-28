package com.github.civcraft.zeus.rabbit.sessions;

import java.util.UUID;

import com.github.civcraft.zeus.rabbit.PlayerSpecificPacketSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class ZeusPlayerLoginSession extends PlayerSpecificPacketSession {

	public ZeusPlayerLoginSession(ConnectedServer source, String transactionID, UUID player) {
		super(source, transactionID, player);
	}

	@Override
	public void handleTimeout() {
		throw new IllegalStateException("Should never be waiting for reply");
	}

}
