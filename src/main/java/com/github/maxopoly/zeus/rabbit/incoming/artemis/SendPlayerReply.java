package com.github.maxopoly.zeus.rabbit.incoming.artemis;

import org.json.JSONObject;

import com.github.maxopoly.zeus.model.TransferRejectionReason;
import com.github.maxopoly.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.RejectPlayerTransfer;
import com.github.maxopoly.zeus.rabbit.sessions.PlayerTransferSession;
import com.github.maxopoly.zeus.servers.ConnectedServer;

/**
 * Sent in reply to SendPlayerRequest to tell whether the target server wants to
 * accept the transfer or not
 *
 */
public class SendPlayerReply extends InteractiveRabbitCommand<PlayerTransferSession> {

	@Override
	public boolean handleRequest(PlayerTransferSession connState, ConnectedServer sendingServer, JSONObject data) {
		boolean accept = data.getBoolean("accept");
		if (accept) {
			// TODO bungee shit
		} else {
			// TODO send back to origin, bungee shit
			sendReply(connState.getSourceServer(),
					new RejectPlayerTransfer(connState.getTransactionID(), TransferRejectionReason.TARGET_REJECT));
			return false;
		}
		return true;
	}

	@Override
	public String getIdentifier() {
		return "reply_receive_player_request";
	}

	@Override
	public boolean createSession() {
		return false;
	}

}
