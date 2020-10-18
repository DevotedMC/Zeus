package com.github.civcraft.zeus.rabbit.incoming.artemis;

import org.json.JSONObject;

import com.github.civcraft.zeus.model.TransferRejectionReason;
import com.github.civcraft.zeus.rabbit.PacketSession;
import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.RejectPlayerTransfer;
import com.github.civcraft.zeus.rabbit.sessions.PlayerTransferSession;
import com.github.civcraft.zeus.servers.ChildServer;

/**
 * Sent in reply to SendPlayerRequest to tell whether the target server wants to
 * accept the transfer or not
 *
 */
public class SendPlayerReply extends InteractiveRabbitCommand<PlayerTransferSession> {

	@Override
	public boolean handleRequest(PlayerTransferSession connState, ChildServer sendingServer, JSONObject data) {
		boolean accept = data.getBoolean("accept");
		if (accept) {
			//TODO maybe ask source server for extra data to pass along first?
			// TODO bungee shit
		} else {
			// TODO send back to origin
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

	@Override
	public boolean destroySession() {
		return false;
	}

}
