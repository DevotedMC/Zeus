package com.github.civcraft.zeus.requests;

import java.util.function.Supplier;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.PacketSession;
import com.github.civcraft.zeus.servers.ChildServer;

public abstract class StaticRabbitCommand extends RabbitRequest {

	@Override
	public final boolean handle(PacketSession connState, ChildServer sendingServer, JSONObject data) {
		handleRequest(sendingServer, data);
		return true;
	}
	
	public abstract void handleRequest(ChildServer sendingServer, JSONObject data);

	@Override
	public boolean createSession() {
		return false;
	}

	@Override
	public boolean destroySession() {
		return false;
	}
	
	public boolean useSession() {
		return false;
	}
	
	public PacketSession getNewSession(ChildServer source, long transactionID, JSONObject data) {
		throw new IllegalStateException();
	}

}
