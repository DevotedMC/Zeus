package com.github.civcraft.zeus.rabbit.outgoing.artemis;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.RabbitMessage;

public class RejectLocationRequest extends RabbitMessage {

	public RejectLocationRequest(String transactionID) {
		super(transactionID);
	}

	@Override
	protected void enrichJson(JSONObject json) {
		
	}

	@Override
	public String getIdentifier() {
		return "reject_location_request";
	}

}
