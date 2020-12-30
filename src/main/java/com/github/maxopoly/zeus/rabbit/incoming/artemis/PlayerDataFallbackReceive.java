package com.github.maxopoly.zeus.rabbit.incoming.artemis;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.model.GlobalPlayerData;
import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.RejectPlayerDataRequest;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.SendPlayerData;
import com.github.maxopoly.zeus.rabbit.sessions.ZeusPlayerDataTransferSession;
import com.github.maxopoly.zeus.servers.ConnectedServer;
import com.github.maxopoly.zeus.util.Base64Encoder;

public class PlayerDataFallbackReceive extends InteractiveRabbitCommand <ZeusPlayerDataTransferSession> {
	
	public final static String ID = "fallback_send_player_data";

	@Override
	public boolean handleRequest(ZeusPlayerDataTransferSession connState, ConnectedServer sendingServer, JSONObject data) {
		boolean available = data.getBoolean("available");
		if (!available) {
			sendReply(sendingServer, new RejectPlayerDataRequest(connState.getTransactionID()));
			return false;
		}
		else {
			GlobalPlayerData zeusPlayerData = ZeusMain.getInstance().getPlayerManager()
					.getOnlinePlayerData(connState.getPlayer());
			if (zeusPlayerData == null) { // player is offline?
				sendReply(sendingServer, new RejectPlayerDataRequest(connState.getTransactionID()));
				return false;
			}
			ZeusLocation location = ZeusLocation.parseLocation(data.getJSONObject("loc"));
			byte [] rawPlayerData = Base64Encoder.decode(data.getString("data"));
			//important to send this to the server stored in the session, not the one we received this packet from
			sendReply(connState.getServerTalkedTo(),
					new SendPlayerData(connState.getTransactionID(), connState.getPlayer(), rawPlayerData, location));
			// we expect explicit confirmation of the target server regarding them actually
			// handling the players data
			return true;
		}
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
