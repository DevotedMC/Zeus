package com.github.maxopoly.zeus.rabbit.incoming.apollo;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.rabbit.incoming.GenericInteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.outgoing.apollo.WorldSpawnReply;
import com.github.maxopoly.zeus.servers.ArtemisServer;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class WorldSpawnRequestHandler extends GenericInteractiveRabbitCommand {

	public static final String REQUEST_ID = "ze_req_world_spawn";
	public static final String REPLY_ID = "ze_rep_world_spawn";
	
	
	@Override
	public void handleRequest(String ticket, ConnectedServer sendingServer, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		//if the player already has player data, use it
		ZeusLocation loc = ZeusMain.getInstance().getDAO().getLocation(player);
		//will random spawn for null location and reuse preexisting location if player has one
		ArtemisServer targetServer = ZeusMain.getInstance().getServerPlacementManager().getTargetServer(loc, false);
		sendReply(sendingServer, new WorldSpawnReply(ticket, targetServer.getID()));
	}

	@Override
	public String getIdentifier() {
		return REQUEST_ID;
	}

}
