package com.github.civcraft.zeus.rabbit.outgoing.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.rabbit.RabbitMessage;
import com.github.civcraft.zeus.util.Base64Encoder;

public class SendPlayerData extends RabbitMessage {

	public static final String ID = "send_player_data";

	private UUID player;
	private byte[] data;
	private ZeusLocation location;

	public SendPlayerData(String transactionID, UUID uuid, byte[] data, ZeusLocation location) {
		super(transactionID);
		this.player = uuid;
		this.data = data;
		this.location = location;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		if (data.length == 0) {
			json.put("new_player", true);
		} else {
			json.put("data", Base64Encoder.encode(data));
		}
		if (location != null) {
			JSONObject obj = new JSONObject();
			location.writeToJson(obj);
			json.put("loc", obj);
		}
		json.put("player", player.toString());

	}

	@Override
	public String getIdentifier() {
		return ID;
	}
}
