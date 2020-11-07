package com.github.civcraft.zeus.rabbit.incoming.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.database.ZeusDAO;
import com.github.civcraft.zeus.model.GlobalPlayerData;
import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.RejectPlayerDataRequest;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.SendPlayerData;
import com.github.civcraft.zeus.rabbit.sessions.PlayerDataTransferSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class PlayerDataRequestHandler extends InteractiveRabbitCommand<PlayerDataTransferSession> {
	
	public static final String ID = "get_player_data";

	@Override
	public boolean handleRequest(PlayerDataTransferSession connState, ConnectedServer sendingServer, JSONObject data) {
		ZeusDAO dao = ZeusMain.getInstance().getDAO();
		byte[] playerData = dao.loadAndLockPlayerNBT(connState.getPlayer(), sendingServer);
		if (playerData == null) {
			sendReply(sendingServer, new RejectPlayerDataRequest(connState.getTransactionID()));
			return false;
		}
		GlobalPlayerData zeusPlayerData = ZeusMain.getInstance().getPlayerManager().getLoggedInPlayerByUUID(connState.getPlayer());
		if (zeusPlayerData == null) { //player is offline?
			sendReply(sendingServer, new RejectPlayerDataRequest(connState.getTransactionID()));
			return false;
		}
		
		ZeusLocation location = zeusPlayerData.consumeIntendedNextLocation();
		if (location == null) {
			location = dao.getLocation(connState.getPlayer());
		}
		sendReply(sendingServer,
				new SendPlayerData(connState.getTransactionID(), connState.getPlayer(), playerData, location));
		// we expect explicit confirmation of the target server regarding them actually
		// handling the players data, just a TCP ACK is not enough
		return true;
	}

	protected PlayerDataTransferSession getFreshSession(ConnectedServer source, String transactionID, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		return new PlayerDataTransferSession(source, transactionID, player);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	public boolean createSession() {
		return true;
	}

}
