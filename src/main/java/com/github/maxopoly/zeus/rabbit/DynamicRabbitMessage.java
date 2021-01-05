package com.github.maxopoly.zeus.rabbit;

import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

public class DynamicRabbitMessage extends RabbitMessage {

	private Map<String, Object> values;
	private String identifier;

	public DynamicRabbitMessage(String transactionID, String identifier, Map<String, Object> values) {
		super(transactionID);
		this.identifier = identifier;
		this.values = values;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		for (Entry<String, Object> entry : values.entrySet()) {
			json.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

}
