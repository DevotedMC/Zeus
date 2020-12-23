package com.github.civcraft.zeus.rabbit.outgoing.artemis;

import org.json.JSONObject;

import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.rabbit.RabbitMessage;
import com.google.common.base.Preconditions;

public class SendPlayerLocation extends RabbitMessage {

	public static final String ID = "reply_location_request";

	private ZeusLocation location;

	public SendPlayerLocation(String transactionID, ZeusLocation location) {
		super(transactionID);
		Preconditions.checkNotNull(location);
		this.location = location;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		location.writeToJson(json);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
