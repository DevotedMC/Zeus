package com.github.civcraft.zeus.rabbit;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.sessions.StandardRequestSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class StandardRequestHandler extends InteractiveRabbitCommand<StandardRequestSession> {

	public static final String ID = "standard_request";

	@Override
	public boolean handleRequest(StandardRequestSession connState, ConnectedServer sendingServer, JSONObject data) {
		connState.handleReply(data);
		return false;
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	public boolean createSession() {
		return false;
	}

}
