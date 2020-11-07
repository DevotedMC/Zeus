package com.github.civcraft.zeus.rabbit.outgoing.artemis;

import org.json.JSONObject;

import com.github.civcraft.zeus.model.TransferRejectionReason;
import com.github.civcraft.zeus.rabbit.RabbitMessage;
import com.google.common.base.Preconditions;

public class RejectPlayerTransfer extends RabbitMessage {
	
	public static final String ID = "reject_transfer";
	
	private TransferRejectionReason reason;

	public RejectPlayerTransfer(String transactionID, TransferRejectionReason reason) {
		super(transactionID);
		Preconditions.checkNotNull(reason);
		this.reason = reason;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("reason", reason.toString());
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
