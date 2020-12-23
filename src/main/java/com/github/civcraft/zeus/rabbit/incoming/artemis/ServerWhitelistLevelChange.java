package com.github.civcraft.zeus.rabbit.incoming.artemis;

import org.json.JSONObject;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.rabbit.incoming.StaticRabbitCommand;
import com.github.civcraft.zeus.servers.ArtemisServer;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class ServerWhitelistLevelChange extends StaticRabbitCommand {

	@Override
	public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
		int level = data.getInt("level");
		ZeusMain.getInstance().getWhitelistManager().setWhitelistLevelServer((ArtemisServer) sendingServer, level);
	}

	@Override
	public String getIdentifier() {
		return "set_whitelist_level";
	}

}
