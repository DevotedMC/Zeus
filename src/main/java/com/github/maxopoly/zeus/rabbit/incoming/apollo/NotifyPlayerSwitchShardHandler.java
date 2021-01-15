package com.github.maxopoly.zeus.rabbit.incoming.apollo;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.plugin.event.events.PlayerFinalDisconnectEvent;
import com.github.maxopoly.zeus.rabbit.incoming.StaticRabbitCommand;
import com.github.maxopoly.zeus.servers.ArtemisServer;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class NotifyPlayerSwitchShardHandler extends StaticRabbitCommand {
	
	public static final String ID = "ze_player_switch_shard";
	
	@Override
	public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		String server = data.getString("server");
		ArtemisServer shard = (ArtemisServer) ZeusMain.getInstance().getServerManager().getServer(server); 
		ZeusMain.getInstance().getPlayerManager().getOnlinePlayerData(player).setMCServer(shard);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
