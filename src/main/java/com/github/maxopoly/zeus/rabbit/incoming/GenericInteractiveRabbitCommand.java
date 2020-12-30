package com.github.maxopoly.zeus.rabbit.incoming;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.sessions.GenericSession;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public abstract class GenericInteractiveRabbitCommand extends InteractiveRabbitCommand<GenericSession> {

	@Override
	public boolean createSession() {
		return true;
	}

	@Override
	protected GenericSession getFreshSession(ConnectedServer source, String transactionID, JSONObject data) {
		return new GenericSession(source, transactionID);
	}

	@Override
	public boolean handleRequest(GenericSession connState, ConnectedServer sendingServer, JSONObject data) {
		handleRequest(connState.getTransactionID(), sendingServer, data);
		return true;
	}

	public abstract void handleRequest(String ticket, ConnectedServer sendingServer, JSONObject data);

}
