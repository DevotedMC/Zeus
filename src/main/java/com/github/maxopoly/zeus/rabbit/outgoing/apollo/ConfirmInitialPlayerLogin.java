package com.github.maxopoly.zeus.rabbit.outgoing.apollo;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.RabbitMessage;

public class ConfirmInitialPlayerLogin extends RabbitMessage {

	public static final String ID = "accept_player_initial_login";

	private String targetServer;
	private String playerName;

	public ConfirmInitialPlayerLogin(String transactionID, String targetServer, String playerName) {
		super(transactionID);
		this.targetServer = targetServer;
		this.playerName = playerName;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("name", playerName);
		json.put("target", targetServer);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
