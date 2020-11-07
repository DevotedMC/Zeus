package com.github.civcraft.zeus.rabbit.incoming.artemis;

import org.json.JSONObject;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.model.GlobalPlayerData;
import com.github.civcraft.zeus.model.TransferRejectionReason;
import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.outgoing.apollo.TransferPlayerToServer;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.AcceptPlayerTransfer;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.RejectPlayerTransfer;
import com.github.civcraft.zeus.rabbit.sessions.PlayerTransferSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class AcceptPlayerJoin extends InteractiveRabbitCommand<PlayerTransferSession> {

	public static final String ID = "accept_player_join_request";

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	public boolean handleRequest(PlayerTransferSession connState, ConnectedServer sendingServer, JSONObject data) {
		GlobalPlayerData state = ZeusMain.getInstance().getPlayerManager()
				.getLoggedInPlayerByUUID(connState.getPlayer());
		if (state == null) {
			// player logged out, just stop
			sendReply(connState.getSourceServer(),
					new RejectPlayerTransfer(connState.getTransactionID(), TransferRejectionReason.PLAYER_LOGOFF));
			return false;
		}
		state.setIntendedNextLocation(connState.getLocation());
		sendReply(connState.getSourceServer(), new AcceptPlayerTransfer(connState.getTransactionID()));
		sendReply(state.getBungeeServer(), new TransferPlayerToServer(connState.getTransactionID(),
				connState.getPlayer(), connState.getTargetServer()));
		return true;
	}

	@Override
	public boolean createSession() {
		return false;
	}

}
