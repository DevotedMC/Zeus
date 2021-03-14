package com.github.maxopoly.zeus.rabbit;

import java.util.Objects;
import org.json.JSONObject;

import com.google.common.base.Preconditions;

/**
 * Represents a message that is sent to a client, possibly as a reply
 *
 */
public abstract class RabbitMessage {

	public static final String TYPE_KEY = "%%type";
	public static final String TRANSACTION_KEY = "%%transaction_id";

	private final String transactionID;

	public RabbitMessage(String transactionID) {
		this.transactionID = Objects.requireNonNull(transactionID,
				"RabbitMessage requires a transaction id!");
	}

	/**
	 * This is a deserialisation constructor, the counterpart to {@link #enrichJson(JSONObject)}. You should include
	 * this type of constructor in all your packets to keep serialisation and deserialisation in one place.
	 *
	 * @param json The JSON object to deserialise from.
	 */
	public RabbitMessage(final JSONObject json) {
		this(Objects.requireNonNull(json.getString(TRANSACTION_KEY),
				"RabbitMessage requires a valid object to deserialise!"));
	}

	/**
	 * Constructs a new JSON containing all data for this packet, ready to be sent.
	 * Includes packet type and transaction id
	 * 
	 * @return JSON packet, ready for sending
	 */
	public JSONObject getJSON() {
		JSONObject json = new JSONObject();
		json.put(TYPE_KEY, getIdentifier());
		json.put(TRANSACTION_KEY, this.transactionID);
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

	/**
	 * @return ID uniquely identifying this message exchange
	 */
	public String getTransactionID() {
		return this.transactionID;
	}

}
