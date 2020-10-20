package com.github.civcraft.zeus.rabbit.outgoing.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.RabbitMessage;

public class GlobalPlayerLogin extends RabbitMessage {
	
	private List<G>

	public GlobalPlayerLogin(String transactionID, String name, UUID uuid) {
		super(transactionID);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void enrichJson(JSONObject json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIdentifier() {
		return "player_network_join";
	}

}
