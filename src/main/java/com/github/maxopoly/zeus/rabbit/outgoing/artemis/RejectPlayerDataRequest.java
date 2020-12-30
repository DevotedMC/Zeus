package com.github.maxopoly.zeus.rabbit.outgoing.artemis;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.RabbitMessage;

public class RejectPlayerDataRequest extends RabbitMessage {

	public static final String ID = "reject_player_data_request";

	public RejectPlayerDataRequest(String transactionID) {
		super(transactionID);
	}

	@Override
	protected void enrichJson(JSONObject json) {
		// session id is identifier enough
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
