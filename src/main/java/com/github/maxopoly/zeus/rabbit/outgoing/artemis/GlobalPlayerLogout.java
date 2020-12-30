package com.github.maxopoly.zeus.rabbit.outgoing.artemis;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.RabbitMessage;

public class GlobalPlayerLogout extends RabbitMessage {

	public GlobalPlayerLogout(String transactionID) {
		super(transactionID);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void enrichJson(JSONObject json) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

}
