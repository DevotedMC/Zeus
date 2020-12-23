package com.github.civcraft.zeus.rabbit.incoming;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.PacketSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public abstract class StaticRabbitCommand extends RabbitRequest {

	@Override
	public final boolean handle(PacketSession connState, ConnectedServer sendingServer, JSONObject data) {
		handleRequest(sendingServer, data);
		return true;
	}

	public abstract void handleRequest(ConnectedServer sendingServer, JSONObject data);

	@Override
	public boolean createSession() {
		return false;
	}

	@Override
	public boolean useSession() {
		return false;
	}

	@Override
	public PacketSession getNewSession(ConnectedServer source, String transactionID, JSONObject data) {
		throw new IllegalStateException();
	}

}
