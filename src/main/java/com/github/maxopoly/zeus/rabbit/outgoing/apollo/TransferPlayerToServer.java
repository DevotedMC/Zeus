package com.github.maxopoly.zeus.rabbit.outgoing.apollo;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.RabbitMessage;
import com.github.maxopoly.zeus.servers.ArtemisServer;

public class TransferPlayerToServer extends RabbitMessage {

	public static String ID = "transfer_player_bungee";

	private ArtemisServer targetServer;
	private UUID player;

	public TransferPlayerToServer(String transactionID, UUID player, ArtemisServer targetServer) {
		super(transactionID);
		this.player = player;
		this.targetServer = targetServer;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("target_server", targetServer.getID());
		json.put("player", player);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
