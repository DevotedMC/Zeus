package com.github.maxopoly.zeus.rabbit.incoming.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.database.ZeusDAO;
import com.github.maxopoly.zeus.model.GlobalPlayerData;
import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.plugin.event.events.ServerLoadPlayerDataEvent;
import com.github.maxopoly.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.RejectPlayerDataRequest;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.SendPlayerData;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.ZeusRequestPlayerData;
import com.github.maxopoly.zeus.rabbit.sessions.ZeusPlayerDataTransferSession;
import com.github.maxopoly.zeus.servers.ArtemisServer;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class PlayerDataRequestHandler extends InteractiveRabbitCommand<ZeusPlayerDataTransferSession> {

	public static final String ID = "get_player_data";

	@Override
	public boolean handleRequest(ZeusPlayerDataTransferSession connState, ConnectedServer sendingServer,
			JSONObject data) {
		ZeusDAO dao = ZeusMain.getInstance().getDAO();
		byte[] playerData = dao.loadAndLockPlayerNBT(connState.getPlayer(), sendingServer);
		if (playerData == null) {
			sendReply(sendingServer, new RejectPlayerDataRequest(connState.getTransactionID()));
			requestDataUnlock(connState);
			return false;
		}
		GlobalPlayerData zeusPlayerData = ZeusMain.getInstance().getPlayerManager()
				.getOnlinePlayerData(connState.getPlayer());
		if (zeusPlayerData == null) { // player is offline?
			sendReply(sendingServer, new RejectPlayerDataRequest(connState.getTransactionID()));
			return false;
		}
		ZeusLocation location = zeusPlayerData.consumeIntendedNextLocation();
		if (location == null) {
			location = dao.getLocation(connState.getPlayer());
		}
		ZeusMain.getInstance().getEventManager()
				.broadcast(new ServerLoadPlayerDataEvent(connState.getPlayer(), (ArtemisServer) sendingServer));
		sendReply(sendingServer,
				new SendPlayerData(connState.getTransactionID(), connState.getPlayer(), playerData, location));
		// we expect explicit confirmation of the target server regarding them actually
		// handling the players data
		return true;
	}

	private void requestDataUnlock(ZeusPlayerDataTransferSession connState) {
		ZeusDAO dao = ZeusMain.getInstance().getDAO();
		String serverWithLock = dao.getServerLockFor(connState.getPlayer());
		ConnectedServer holdingLock = ZeusMain.getInstance().getServerManager().getServer(serverWithLock);
		if (holdingLock == null) {
			return;
		}
		sendReply(holdingLock, new ZeusRequestPlayerData(
				ZeusMain.getInstance().getTransactionIdManager().pullNewTicket(), connState.getPlayer()));
	}

	@Override
	protected ZeusPlayerDataTransferSession getFreshSession(ConnectedServer source, String transactionID,
			JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		return new ZeusPlayerDataTransferSession(source, transactionID, player);
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
