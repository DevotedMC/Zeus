package com.github.civcraft.zeus.rabbit;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.civcraft.zeus.requests.RabbitRequest;
import com.github.civcraft.zeus.servers.ChildServer;

/**
 * Like a command handler, but for incoming JSON
 *
 */
public class RabbitInputHandler {

	private Map<String, RabbitRequest> commands;
	private Logger logger;

	public RabbitInputHandler(Logger logger) {
		this.commands = new HashMap<>();
		this.logger = logger;
	}

	private void registerCommands() {
		// registerCommand(...);
	}

	private void registerCommand(RabbitRequest command) {
		this.commands.put(command.getIdentifier(), command);
	}

	public void handle(ChildServer sourceServer, String rawJson) {
		JSONObject input;
		try {
			input = new JSONObject(rawJson);
		} catch (JSONException e) {
			logger.error("Received invalid json", e);
			return;
		}
		String type = input.getString("type");
		if (type == null) {
			logger.error("Input " + input.toString() + " had no type");
			return;
		}
		RabbitRequest command = commands.get(type);
		if (command == null) {
			logger.warn("Input " + type + " had no handler");
			return;
		}
		if (!command.useSession()) {
			command.handle(null, sourceServer, input);
			return;
		}
		long transactionID = input.optLong("transaction_id", -1);
		if (transactionID == -1) {
			logger.error("Expected transaction id in packet of type " + type + ", but found none");
			return;
		}
		PacketSession session;
		if (command.createSession()) {
			session = command.getNewSession(sourceServer, transactionID, input);
			if (session == null) {
				logger.error("Failed to create new session for " + type);
				return;
			}
		}
		else {
			session = sourceServer.getTransactionState().getSession(transactionID);
			if (session == null) {
				logger.error("Expected existing transaction for packet of type " + type + ", but found none");
				return;
			}
		}
		command.handle(session, sourceServer, input);
		if (command.destroySession()) {
			sourceServer.getTransactionState().deleteSession(transactionID);
		}
	}

}
