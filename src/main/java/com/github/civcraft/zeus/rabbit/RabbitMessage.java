package com.github.civcraft.zeus.rabbit;

import org.json.JSONObject;

/**
 * Represents a message that is sent to a client, possibly as a reply
 *
 */
public abstract class RabbitMessage {

	private final String transactionID;

	public RabbitMessage(String transactionID) {
		this.transactionID = transactionID;
	}

	/**
	 * Constructs a new JSON containing all data for this packet, ready to be sent.
	 * Includes packet type and transaction id
	 * 
	 * @return JSON packet, ready for sending
	 */
	public JSONObject getJSON() {
		JSONObject json = new JSONObject();
		json.put("type", getIdentifier());
		json.put("transaction_id", transactionID);
		enrichJson(json);
		return json;
	}

	/**
	 * Inserts custom per packet data into the json to send
	 * 
	 * @param json Json to fill data into
	 */
	protected abstract void enrichJson(JSONObject json);

	/**
	 * @return Unique identifier for this kind of packet
	 */
	public abstract String getIdentifier();

}
