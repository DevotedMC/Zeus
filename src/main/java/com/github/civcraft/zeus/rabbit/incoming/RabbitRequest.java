package com.github.civcraft.zeus.rabbit.incoming;

import java.util.function.Supplier;

import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.rabbit.PacketSession;
import com.github.civcraft.zeus.servers.ChildServer;


public abstract class RabbitRequest {
	public abstract boolean handle(PacketSession connState, ChildServer sendingServer, JSONObject data);
	
	public abstract String getIdentifier();
	
	public abstract boolean createSession();
	
	public abstract boolean destroySession();
	
	public abstract boolean useSession();
	
	public abstract PacketSession getNewSession(ChildServer source, String transactionID, JSONObject data);
	
	protected Logger getLogger() {
		return ZeusMain.getInstance().getLogger();
	}

}

