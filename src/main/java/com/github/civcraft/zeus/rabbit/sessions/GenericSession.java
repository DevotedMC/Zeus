package com.github.civcraft.zeus.rabbit.sessions;

import com.github.civcraft.zeus.rabbit.PacketSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class GenericSession extends PacketSession {

	public GenericSession(ConnectedServer source, String transactionID) {
		super(source, transactionID);
	}

	@Override
	public void handleTimeout() {
		// nothing
	}

}
