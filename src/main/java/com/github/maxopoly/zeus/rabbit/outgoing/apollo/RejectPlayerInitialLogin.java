package com.github.maxopoly.zeus.rabbit.outgoing.apollo;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.RabbitMessage;

public class RejectPlayerInitialLogin extends RabbitMessage {

	public static final String ID = "reject_initial_login";

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
		return ID;
	}

}
