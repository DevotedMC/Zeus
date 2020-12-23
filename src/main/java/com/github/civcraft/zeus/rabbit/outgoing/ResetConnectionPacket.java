package com.github.civcraft.zeus.rabbit.outgoing;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.RabbitMessage;

/**
 * Can be sent by either side to invalidate all existing tickets issued by it,
 * usually after a restart
 *
 */
public class ResetConnectionPacket extends RabbitMessage {

	public static String ID = "reset_connection";

	public ResetConnectionPacket(String transactionID) {
		super(transactionID);
	}

	@Override
	protected void enrichJson(JSONObject json) {
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
