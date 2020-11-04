package com.github.civcraft.zeus.rabbit.outgoing.artemis;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.RabbitMessage;

public class RejectPlayerDataRequest extends RabbitMessage {
	
	public static final String ID = "reject_player_data_request";

	public RejectPlayerDataRequest(String transactionID) {
		super(transactionID);
	}

	@Override
	protected void enrichJson(JSONObject json) {
		//session id is identifier enough
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
	

}
