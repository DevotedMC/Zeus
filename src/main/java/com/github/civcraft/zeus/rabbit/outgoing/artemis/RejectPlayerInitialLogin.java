package com.github.civcraft.zeus.rabbit.outgoing.artemis;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.RabbitMessage;

public class RejectPlayerInitialLogin extends RabbitMessage {
	
	private String reason;

	public RejectPlayerInitialLogin(String transactionID, String reason) {
		super(transactionID);
		this.reason = reason;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("reason", reason);
	}

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

}
