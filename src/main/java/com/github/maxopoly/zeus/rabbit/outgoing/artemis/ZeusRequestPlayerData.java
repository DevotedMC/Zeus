package com.github.maxopoly.zeus.rabbit.outgoing.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.RabbitMessage;

/**
 * If a server still has a lock on player data, but the player is offline (due
 * to server crashes etc.), Zeus uses this packet to request the players data
 * from Artemis file cache to wrap up their session and allow new logins
 *
 */
public class ZeusRequestPlayerData extends RabbitMessage {
	
	public final static String ID = "req_player_data";

	private UUID player;
	
	public ZeusRequestPlayerData(String transactionID, UUID player) {
		super(transactionID);
		this.player = player;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("player", player.toString());
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
