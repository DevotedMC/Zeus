package com.github.maxopoly.zeus.rabbit.incoming;

import java.util.function.BiConsumer;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.rabbit.PacketSession;
import com.github.maxopoly.zeus.rabbit.RabbitMessage;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public abstract class InteractiveRabbitCommand<S extends PacketSession> extends RabbitRequest {

	private static BiConsumer<ConnectedServer, RabbitMessage> sendLambda;

	public static void setSendingLambda(BiConsumer<ConnectedServer, RabbitMessage> send) {
		sendLambda = send;
	}

	public abstract boolean handleRequest(S connState, ConnectedServer sendingServer, JSONObject data);

	@Override
	@SuppressWarnings("unchecked")
	public boolean handle(PacketSession connState, ConnectedServer sendingServer, JSONObject data) {
		return handleRequest((S) connState, sendingServer, data);
	}

	protected void sendReply(ConnectedServer target, RabbitMessage message) {
		sendLambda.accept(target, message);
	}

	protected void broadcastReply(RabbitMessage message) {
		ZeusMain.getInstance().getBroadcastInterestTracker().broadcastMessage(message);
	}

	@Override
	public boolean useSession() {
		return true;
	}

	@Override
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
