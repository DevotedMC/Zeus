package com.github.maxopoly.zeus.rabbit.incoming.artemis;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.rabbit.incoming.GenericInteractiveRabbitCommand;
import com.github.maxopoly.zeus.servers.ArtemisServer;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class ArtemisShutdownHandler extends GenericInteractiveRabbitCommand {

	public static final String ID = "artemis_shutdown";

	@Override
	public void handleRequest(String ticket, ConnectedServer sendingServer, JSONObject data) {
		ZeusMain.getInstance().getServerPlacementManager().removeServer((ArtemisServer) sendingServer);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
}
