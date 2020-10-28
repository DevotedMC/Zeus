package com.github.civcraft.zeus.rabbit.incoming.apollo;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.ConfirmInitialPlayerLogin;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.RejectPlayerInitialLogin;
import com.github.civcraft.zeus.rabbit.sessions.ZeusPlayerLoginSession;
import com.github.civcraft.zeus.servers.ArtemisServer;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class PlayerLoginRequest extends InteractiveRabbitCommand<ZeusPlayerLoginSession> {

	@Override
	public boolean handleRequest(ZeusPlayerLoginSession connState, ConnectedServer sendingServer, JSONObject data) {
		ZeusLocation location = ZeusMain.getInstance().getDAO().getLocation(connState.getPlayer());
		ArtemisServer target = ZeusMain.getInstance().getServerPlacementManager().getTargetServer(location);
		if (target == null) {
			sendReply(connState.getServerID(),
					new RejectPlayerInitialLogin(connState.getTransactionID(), "No target found"));
			return false;
		}
		sendReply(connState.getServerID(), new ConfirmInitialPlayerLogin(connState.getTransactionID(), target.getID()));
		return false;
	}

	@Override
	public String getIdentifier() {
		return "initial_login_request";
	}

	@Override
	public boolean createSession() {
		return true;
	}

	protected ZeusPlayerLoginSession getFreshSession(ConnectedServer source, String transactionID, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		return new ZeusPlayerLoginSession(source, transactionID, player);
	}

}
