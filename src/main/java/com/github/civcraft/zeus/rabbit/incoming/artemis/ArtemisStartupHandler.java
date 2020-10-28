package com.github.civcraft.zeus.rabbit.incoming.artemis;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.incoming.GenericInteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.sessions.GenericSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class ArtemisStartupHandler extends GenericInteractiveRabbitCommand {

	@Override
	public boolean handleRequest(ConnectedServer sendingServer, JSONObject data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean createSession() {
		return true;
	}

}
