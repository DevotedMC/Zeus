package com.github.maxopoly.zeus.rabbit.common;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.RabbitMessage;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.SendPlayerData;

public class ReplyReceivedPlayerData extends RabbitMessage {

	private boolean accepted;
	
	public ReplyReceivedPlayerData(String transactionID, boolean accepted) {
		super(transactionID);
		this.accepted = accepted;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("accepted", accepted);
	}

	@Override
	public String getIdentifier() {
		return SendPlayerData.REPLY_ID;
	}

}
