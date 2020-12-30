package com.github.maxopoly.zeus.rabbit.incoming;

import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.rabbit.PacketSession;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public abstract class RabbitRequest {
	public abstract boolean handle(PacketSession connState, ConnectedServer sendingServer, JSONObject data);

	public abstract String getIdentifier();

	public abstract boolean createSession();

	public abstract boolean useSession();

	public abstract PacketSession getNewSession(ConnectedServer source, String transactionID, JSONObject data);

	protected Logger getLogger() {
		return ZeusMain.getInstance().getLogger();
	}

}
