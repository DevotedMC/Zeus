package com.github.civcraft.zeus.rabbit.outgoing.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.model.Location;
import com.github.civcraft.zeus.rabbit.RabbitMessage;
import com.google.common.base.Preconditions;

/**
 * When transferring a player, this is sent as initial request to the target server to check whether target wants to take the play
 *
 */
public class SendPlayerRequest extends RabbitMessage {

	private UUID player;
	private Location location;
	
	public SendPlayerRequest(String transactionID, UUID player, Location location) {
		super(transactionID);
		Preconditions.checkNotNull(player);
		Preconditions.checkNotNull(location);
		this.player = player;
		this.location = location;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("player", player.toString());
		location.writeToJson(json);
	}

	@Override
	public String getIdentifier() {
		return "receive_player_request";
	}

}
