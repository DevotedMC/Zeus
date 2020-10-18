package com.github.civcraft.zeus.rabbit.messages.artemis;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.RabbitMessage;
import com.google.common.base.Preconditions;

public class RejectPlayerTransfer extends RabbitMessage {
	
	private String reason;

	public RejectPlayerTransfer(long transactionID, String reason) {
		super(transactionID);
		Preconditions.checkNotNull(reason);
		this.reason = reason;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("reason", reason);
	}

	@Override
	protected String getIdentifier() {
		return "reject_transfer";
	}

}
