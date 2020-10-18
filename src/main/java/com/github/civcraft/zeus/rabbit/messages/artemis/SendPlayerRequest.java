package com.github.civcraft.zeus.rabbit.messages.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.RabbitMessage;

/**
 * When transferring a player, this is sent as initial request to the target server to check whether target wants to take the play
 *
 */
public class SendPlayerRequest extends RabbitMessage {

	private UUID player;
	
	public SendPlayerRequest(long transactionID, UUID player) {
		super(transactionID);
		this.player = player;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("player", player.toString());
	}

	@Override
	protected String getIdentifier() {
		return "receive_player_request";
	}

}
