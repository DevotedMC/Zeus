package com.github.maxopoly.zeus.rabbit;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.sessions.StandardRequestSession;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class StandardRequestHandler extends InteractiveRabbitCommand<StandardRequestSession> {
	
	private String id;
	
	public StandardRequestHandler(String id) {
		this.id = id;
	}

	@Override
	public boolean handleRequest(StandardRequestSession connState, ConnectedServer sendingServer, JSONObject data) {
		connState.handleReply(data);
		return false;
	}

	@Override
	public String getIdentifier() {
		return id;
	}

	@Override
	public boolean createSession() {
		return false;
	}

}
