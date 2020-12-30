package com.github.maxopoly.zeus.rabbit.outgoing.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.rabbit.RabbitMessage;
import com.google.common.base.Preconditions;

/**
 * When transferring a player, this is sent as initial request to the target
 * server to check whether target wants to take the player
 *
 */
public class SendPlayerRequest extends RabbitMessage {

	public static final String ID = "receive_player_request";

	private UUID player;
	private ZeusLocation location;

	public SendPlayerRequest(String transactionID, UUID player, ZeusLocation location) {
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
		return ID;
	}

}
