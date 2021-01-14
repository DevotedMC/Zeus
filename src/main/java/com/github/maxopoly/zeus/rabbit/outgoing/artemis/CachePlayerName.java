package com.github.maxopoly.zeus.rabbit.outgoing.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.RabbitMessage;

public class CachePlayerName extends RabbitMessage {
	
	public static final String ID = "cache_name_uuid";

	private UUID uuid;
	private String name;
	
	public CachePlayerName(String transactionID, UUID uuid, String name) {
		super(transactionID);
		this.uuid = uuid;
		this.name = name;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("uuid", uuid.toString());
		json.put("name", name);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
