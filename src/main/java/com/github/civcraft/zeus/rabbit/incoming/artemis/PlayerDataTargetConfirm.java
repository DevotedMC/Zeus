package com.github.civcraft.zeus.rabbit.incoming.artemis;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.sessions.PlayerDataTransferSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class PlayerDataTargetConfirm extends InteractiveRabbitCommand<PlayerDataTransferSession> {

	@Override
	public boolean handleRequest(PlayerDataTransferSession connState, ConnectedServer sendingServer, JSONObject data) {
		return true;
	}

	@Override
	public String getIdentifier() {
		return "confirm_player_data_handle";
	}

	@Override
	public boolean createSession() {
		return false;
	}

}
