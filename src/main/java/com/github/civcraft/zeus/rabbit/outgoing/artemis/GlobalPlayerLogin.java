package com.github.civcraft.zeus.rabbit.outgoing.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.RabbitMessage;

public class GlobalPlayerLogin extends RabbitMessage {

	// TODO MOVE TO ZEUS

	// private List<G>
	private String name;
	private UUID uuid;

	public GlobalPlayerLogin(String transactionID, String name, UUID uuid) {
		super(transactionID);
		this.name = name;
		this.uuid = uuid;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void enrichJson(JSONObject json) {
		// TODO Auto-generated method stub
		JSONObject obj = new JSONObject();
		obj.put("uuid", uuid);
		obj.put("name", name);
		json.put("player", obj);
	}

	@Override
	public String getIdentifier() {
		return "player_network_join";
	}

}
