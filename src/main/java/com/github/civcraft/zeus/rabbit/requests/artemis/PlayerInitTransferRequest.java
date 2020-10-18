package com.github.civcraft.zeus.rabbit.requests.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.CardinalDirection;
import com.github.civcraft.zeus.Location;
import com.github.civcraft.zeus.TransferRejectionReason;
import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.rabbit.PacketSession;
import com.github.civcraft.zeus.rabbit.messages.artemis.RejectPlayerTransfer;
import com.github.civcraft.zeus.rabbit.messages.artemis.SendPlayerRequest;
import com.github.civcraft.zeus.rabbit.sessions.PlayerTransferSession;
import com.github.civcraft.zeus.requests.InteractiveRabbitCommand;
import com.github.civcraft.zeus.requests.ParsingUtils;
import com.github.civcraft.zeus.requests.RabbitRequest;
import com.github.civcraft.zeus.servers.ArtemisServer;
import com.github.civcraft.zeus.servers.ChildServer;

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
	public boolean destroySession() {
		return false;
	}

	protected PlayerTransferSession getFreshSession(ChildServer source, long transactionID, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		return new PlayerTransferSession(source, transactionID, player);
	}

	@Override
	public boolean handleRequest(PlayerTransferSession connState, ChildServer sendingServer, JSONObject data) {
		if (!(sendingServer instanceof ArtemisServer)) {
			sendReply(sendingServer, new RejectPlayerTransfer(connState.getTransactionID(),
					TransferRejectionReason.INVALID_SOURCE.toString()));
			getLogger().error("Only Artemis server can send players, but " + sendingServer + " tried anyway");
			return false;
		}
		ArtemisServer sourceServer = (ArtemisServer) sendingServer;
		connState.setSourceServer(sourceServer);
		String exitDirection = data.getString("exit");
		CardinalDirection direction = CardinalDirection.valueOf(exitDirection);
		Location loc = ParsingUtils.parseLocation(data);
		ArtemisServer targetServer = ZeusMain.getInstance().getServerPlacementManager().getTargetServer(sourceServer,
				direction, loc);
		if (targetServer == null) {
			sendReply(sendingServer, new RejectPlayerTransfer(connState.getTransactionID(),
					TransferRejectionReason.NO_TARGET_FOUND.toString()));
			getLogger().error("Failed to find target server for " + loc + " from " + sendingServer);
			return false;
		}
		connState.setTargetServer(targetServer);
		sendReply(targetServer, new SendPlayerRequest(connState.getTransactionID(), connState.getPlayer()));
		return true;
	}

}
