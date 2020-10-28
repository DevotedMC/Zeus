package com.github.civcraft.zeus.rabbit.outgoing.artemis;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.RabbitMessage;

public class ConfirmInitialPlayerLogin extends RabbitMessage {
	
	public static final String ID = "accept_player_initial_login";
	
	private String targetServer;

	public ConfirmInitialPlayerLogin(String transactionID, String targetServer) {
		super(transactionID);
		this.targetServer = targetServer;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("target", targetServer);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
