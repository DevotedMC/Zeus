package com.github.maxopoly.zeus.rabbit.sessions;

import com.github.maxopoly.zeus.rabbit.PacketSession;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class GenericSession extends PacketSession {

	public GenericSession(ConnectedServer source, String transactionID) {
		super(source, transactionID);
	}

	@Override
	public void handleTimeout() {
		// nothing
	}

}
