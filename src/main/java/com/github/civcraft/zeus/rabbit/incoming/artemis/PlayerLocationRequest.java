package com.github.civcraft.zeus.rabbit.incoming.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.model.GlobalPlayerData;
import com.github.civcraft.zeus.model.LocationRejectionReason;
import com.github.civcraft.zeus.model.TransferRejectionReason;
import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.incoming.ParsingUtils;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.RejectLocationRequest;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.RejectPlayerTransfer;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.SendPlayerRequest;
import com.github.civcraft.zeus.rabbit.sessions.LocationRequestSession;
import com.github.civcraft.zeus.servers.ArtemisServer;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class PlayerLocationRequest extends InteractiveRabbitCommand<LocationRequestSession> {

	@Override
	public String getIdentifier() {
		return "init_transfer";
	}

	@Override
	public boolean createSession() {
		return true;
	}

	protected LocationRequestSession getFreshSession(ConnectedServer source, String transactionID, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		return new LocationRequestSession(source, transactionID, player);
	}

	@Override
	public boolean handleRequest(LocationRequestSession connState, ConnectedServer sendingServer, JSONObject data) {
		
		if (!(sendingServer instanceof ArtemisServer)) {
			sendReply(sendingServer, new RejectLocationRequest(connState.getTransactionID(),
					LocationRejectionReason.INVALID_SOURCE));
			getLogger().error("Only Artemis server can request the locations of players, but " + sendingServer + " tried anyway");
			return false;
		}
		
		
		UUID player = UUID.fromString(data.getString("player"));
		GlobalPlayerData playerData = ZeusMain.getInstance().getPlayerDataManager().getLoggedInPlayerByUUID(player);
		if(playerData == null){
			sendReply(sendingServer, new RejectLocationRequest(connState.getTransactionID(),
					LocationRejectionReason.TARGET_NOT_FOUND));
			getLogger().error("Artemis server " + sendingServer + " requested the location of " + data.getString("player") + " but player was not found");
			return false;
		}
		
		if(playerData.getServer() == null){
			sendReply(sendingServer, new RejectLocationRequest(connState.getTransactionID(),
					LocationRejectionReason.SERVER_NOT_FOUND));
			getLogger().error("Only Artemis server can request the locations of players, but " + sendingServer + " tried anyway");
			return false;
		}
		
		/*
		 * so now we need 
		 */
		
		/*
		
		ArtemisServer sourceServer = (ArtemisServer) sendingServer;
		connState.setSourceServer(sourceServer);
		ZeusLocation loc = ZeusLocation.parseLocation(data.getJSONObject("loc"));
		ArtemisServer targetServer = ZeusMain.getInstance().getServerPlacementManager().getTargetServer(sourceServer,
				 loc);
		if (targetServer == null) {
			sendReply(sendingServer, new RejectPlayerTransfer(connState.getTransactionID(),
					TransferRejectionReason.NO_TARGET_FOUND));
			getLogger().error("Failed to find target server for " + loc + " from " + sendingServer);
			return false;
		}
		connState.setTargetServer(targetServer);
		sendReply(targetServer, new SendPlayerRequest(connState.getTransactionID(), connState.getPlayer(), loc));
		*/
		return true;
	}

}
