package com.github.maxopoly.zeus.rabbit.incoming.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.model.GlobalPlayerData;
import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.RejectLocationRequest;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.SendPlayerLocation;
import com.github.maxopoly.zeus.rabbit.sessions.LocationRequestSession;
import com.github.maxopoly.zeus.servers.ConnectedServer;

/**
 * Any ConnectedServer can request the saved location of any known player and
 * whether they're online right now through the packet handled here
 *
 */
public class PlayerLocationRequest extends InteractiveRabbitCommand<LocationRequestSession> {

	public static final String ID = "player_loc_request";

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	public boolean handleRequest(LocationRequestSession connState, ConnectedServer sendingServer, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		GlobalPlayerData playerData = ZeusMain.getInstance().getPlayerManager().getOnlinePlayerData(player);
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
