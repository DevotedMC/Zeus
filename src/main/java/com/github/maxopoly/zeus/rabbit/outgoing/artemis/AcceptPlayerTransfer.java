package com.github.maxopoly.zeus.rabbit.outgoing.artemis;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.RabbitMessage;

public class AcceptPlayerTransfer extends RabbitMessage {

	public static final String ID = "accept_transfer";

	public AcceptPlayerTransfer(String transactionID) {
		super(transactionID);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		// no content
	}

}
