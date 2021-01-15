package com.github.maxopoly.zeus.rabbit.outgoing.apollo;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.RabbitMessage;
import com.github.maxopoly.zeus.rabbit.incoming.apollo.WorldSpawnRequestHandler;

public class WorldSpawnReply extends RabbitMessage {
	
	private String targetServer;

	public WorldSpawnReply(String transactionID, String targetServer) {
		super(transactionID);
		this.targetServer = targetServer;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("target_server", targetServer);
	}

	@Override
	public String getIdentifier() {
		return WorldSpawnRequestHandler.REPLY_ID;
	}

}
