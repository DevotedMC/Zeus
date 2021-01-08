package com.github.maxopoly.zeus.rabbit.incoming.artemis;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.model.GlobalPlayerData;
import com.github.maxopoly.zeus.model.TransferRejectionReason;
import com.github.maxopoly.zeus.plugin.event.events.PlayerJoinServerEvent;
import com.github.maxopoly.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.outgoing.apollo.TransferPlayerToServer;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.AcceptPlayerTransfer;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.RejectPlayerTransfer;
import com.github.maxopoly.zeus.rabbit.sessions.PlayerTransferSession;
import com.github.maxopoly.zeus.servers.ArtemisServer;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class AcceptPlayerJoin extends InteractiveRabbitCommand<PlayerTransferSession> {

	public static final String ID = "accept_player_join_request";

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	public boolean handleRequest(PlayerTransferSession connState, ConnectedServer sendingServer, JSONObject data) {
		GlobalPlayerData state = ZeusMain.getInstance().getPlayerManager()
				.getOnlinePlayerData(connState.getPlayer());
		if (state == null) {
			// player logged out, just stop
			sendReply(connState.getSourceServer(),
					new RejectPlayerTransfer(connState.getTransactionID(), TransferRejectionReason.PLAYER_LOGOFF));
			return false;
		}
		state.setIntendedNextLocation(connState.getLocation());
		ArtemisServer previousServer = state.getMCServer();
		state.setMCServer((ArtemisServer) sendingServer);
		ZeusMain.getInstance().getEventManager().broadcast(new PlayerJoinServerEvent(state, previousServer, state.getMCServer()));
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
