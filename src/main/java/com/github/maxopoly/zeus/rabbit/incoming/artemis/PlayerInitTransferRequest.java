package com.github.maxopoly.zeus.rabbit.incoming.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.model.GlobalPlayerData;
import com.github.maxopoly.zeus.model.TransferRejectionReason;
import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.plugin.event.events.RequestPlayerTransferEvent;
import com.github.maxopoly.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.RejectPlayerTransfer;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.SendPlayerRequest;
import com.github.maxopoly.zeus.rabbit.sessions.PlayerTransferSession;
import com.github.maxopoly.zeus.servers.ArtemisServer;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class PlayerInitTransferRequest extends InteractiveRabbitCommand<PlayerTransferSession> {

	@Override
	public String getIdentifier() {
		return "init_transfer";
	}

	@Override
	public boolean createSession() {
		return true;
	}

	@Override
	protected PlayerTransferSession getFreshSession(ConnectedServer source, String transactionID, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		ZeusLocation loc = ZeusLocation.parseLocation(data.getJSONObject("loc"));
		return new PlayerTransferSession(source, transactionID, player, loc);
	}

	@Override
	public boolean handleRequest(PlayerTransferSession connState, ConnectedServer sendingServer, JSONObject data) {
		if (!(sendingServer instanceof ArtemisServer)) {
			sendReply(sendingServer,
					new RejectPlayerTransfer(connState.getTransactionID(), TransferRejectionReason.INVALID_SOURCE));
			getLogger().error("Only Artemis server can send players, but " + sendingServer + " tried anyway");
			return false;
		}
		ArtemisServer sourceServer = (ArtemisServer) sendingServer;
		connState.setSourceServer(sourceServer);
		ArtemisServer targetServer = ZeusMain.getInstance().getServerPlacementManager().getTargetServer(sourceServer,
				connState.getLocation());
		GlobalPlayerData playerData = ZeusMain.getInstance().getPlayerManager()
				.getOnlinePlayerData(connState.getPlayer());
		ZeusMain.getInstance().getEventManager().broadcast(
				new RequestPlayerTransferEvent(sourceServer, connState.getLocation(), targetServer, playerData));
		if (targetServer == null) {
			sendReply(sendingServer,
					new RejectPlayerTransfer(connState.getTransactionID(), TransferRejectionReason.NO_TARGET_FOUND));
			getLogger().error("Failed to find target server for " + connState.getLocation() + " from " + sendingServer);
			return false;
		}
		connState.setTargetServer(targetServer);
		sendReply(targetServer,
				new SendPlayerRequest(connState.getTransactionID(), connState.getPlayer(), connState.getLocation()));
		return true;
	}

}
