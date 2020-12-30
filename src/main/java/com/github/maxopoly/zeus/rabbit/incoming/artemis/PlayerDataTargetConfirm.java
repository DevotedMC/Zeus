package com.github.maxopoly.zeus.rabbit.incoming.artemis;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.sessions.PlayerDataTransferSession;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class PlayerDataTargetConfirm extends InteractiveRabbitCommand<PlayerDataTransferSession> {
	
	public static final String ID = "confirm_player_data_handle";

	@Override
	public boolean handleRequest(PlayerDataTransferSession connState, ConnectedServer sendingServer, JSONObject data) {
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
