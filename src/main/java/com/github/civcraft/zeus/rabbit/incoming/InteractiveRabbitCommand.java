package com.github.civcraft.zeus.rabbit.incoming;

import java.util.function.Supplier;

import org.json.JSONObject;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.rabbit.PacketSession;
import com.github.civcraft.zeus.rabbit.RabbitGateway;
import com.github.civcraft.zeus.rabbit.RabbitMessage;
import com.github.civcraft.zeus.servers.ConnectedServer;

public abstract class InteractiveRabbitCommand<S extends PacketSession> extends RabbitRequest {

	public abstract boolean handleRequest(S connState, ConnectedServer sendingServer, JSONObject data);

	@SuppressWarnings("unchecked")
	public boolean handle(PacketSession connState, ConnectedServer sendingServer, JSONObject data) {
		return handleRequest((S) connState, sendingServer, data);
	}

	protected void sendReply(ConnectedServer target, RabbitMessage message) {
		RabbitGateway.getInstance().sendMessage(target, message.getJSON());
	}

	protected void broadcastReply(RabbitMessage message) {
		ZeusMain.getInstance().getBroadcastInterestTracker().broadcastMessage(getIdentifier(), message);
	}

	public boolean useSession() {
		return true;
	}

	public final PacketSession getNewSession(ConnectedServer source, String transactionID, JSONObject data) {
		return getFreshSession(source, transactionID, data);
	}

	/**
	 * Creates a new packet session to be used. Needs to be overwritten if
	 * createSession() returns true
	 */
	protected S getFreshSession(ConnectedServer source, String transactionID, JSONObject data) {
		return null;
	}

}
