package com.github.civcraft.zeus.rabbit.incoming.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.model.GlobalPlayerData;
import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.RejectLocationRequest;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.SendPlayerLocation;
import com.github.civcraft.zeus.rabbit.sessions.LocationRequestSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class PlayerLocationRequest extends InteractiveRabbitCommand<LocationRequestSession> {

	@Override
	public String getIdentifier() {
		return "player_location_reply";
	}

	@Override
	public boolean handleRequest(LocationRequestSession connState, ConnectedServer sendingServer, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		GlobalPlayerData playerData = ZeusMain.getInstance().getPlayerDataManager().getLoggedInPlayerByUUID(player);
		data.put("online", playerData != null);
		ZeusLocation loc = ZeusMain.getInstance().getDAO().getLocation(player);
		if (loc == null) {
			sendReply(sendingServer, new RejectLocationRequest(connState.getTransactionID()));
			return false;
		}
		sendReply(sendingServer, new SendPlayerLocation(connState.getTransactionID(), loc));
		return false;
	}

	@Override
	public boolean createSession() {
		return true;
	}

	@Override
	public LocationRequestSession getFreshSession(ConnectedServer source, String transactionID, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		return new LocationRequestSession(source, transactionID, player);
	}

}
