package com.github.maxopoly.zeus.rabbit.incoming.apollo;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.plugin.event.events.PlayerFinalDisconnectEvent;
import com.github.maxopoly.zeus.rabbit.incoming.StaticRabbitCommand;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class PlayerDisconnectHandler extends StaticRabbitCommand {

	public static final String ID = "zeus_player_logoff";
	
	@Override
	public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		PlayerFinalDisconnectEvent event = new PlayerFinalDisconnectEvent(player);
		ZeusMain.getInstance().getEventManager().broadcast(event);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
