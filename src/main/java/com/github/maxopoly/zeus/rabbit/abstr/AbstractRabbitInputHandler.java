package com.github.maxopoly.zeus.rabbit.abstr;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.maxopoly.zeus.model.TransactionIdManager;
import com.github.maxopoly.zeus.rabbit.PacketSession;
import com.github.maxopoly.zeus.rabbit.StandardRequestHandler;
import com.github.maxopoly.zeus.rabbit.incoming.RabbitRequest;
import com.github.maxopoly.zeus.servers.ConnectedServer;

/**
 * Like a command handler, but for incoming JSON
 *
 */
public abstract class AbstractRabbitInputHandler {

	private Map<String, RabbitRequest> commands;
	private TransactionIdManager transactionManager;

	public AbstractRabbitInputHandler(TransactionIdManager transactionManager) {
		this.commands = new HashMap<>();
		this.transactionManager = transactionManager;
		registerCommands();
	}

	protected abstract void registerCommands();

	public void registerCommand(RabbitRequest command, RabbitRequest ... comms) {
		this.commands.put(command.getIdentifier(), command);
		for(RabbitRequest comm : comms) {
			this.commands.put(comm.getIdentifier(), comm);
		}
	}
	
	public StandardRequestHandler deferCommandToStandardRequest(String id, String ... others) {
		StandardRequestHandler reqHandler = new StandardRequestHandler(id);
		registerCommand(reqHandler);
		for(String other : others) {
			StandardRequestHandler otherReqHandler = new StandardRequestHandler(other);
			registerCommand(otherReqHandler);
		}
		return reqHandler;
	}

	public void unregisterCommand(RabbitRequest command) {
		this.commands.remove(command.getIdentifier());
	}

	public void handle(ConnectedServer sourceServer, String rawJson) {
		JSONObject input;
		try {
			input = new JSONObject(rawJson);
		} catch (JSONException e) {
			logError("Received invalid json " + e.toString());
			return;
		}
		String type = input.getString("%%type");
		if (type == null) {
			logError("Input " + input.toString() + " had no type");
			return;
		}
		RabbitRequest command = commands.get(type);
		if (command == null) {
			logError("Input " + type + " had no handler");
			return;
		}
		if (!command.useSession()) {
			command.handle(null, sourceServer, input);
			return;
		}
		String transactionID = input.optString("%%transaction_id", "");
		if (transactionID.isEmpty()) {
			logError("Expected transaction id in packet of type " + type + ", but found none");
			return;
		}
		PacketSession session;
		if (command.createSession()) {
			session = command.getNewSession(sourceServer, transactionID, input);
			if (session == null) {
				logError("Failed to create new session for " + type);
				return;
			}
			transactionManager.putSession(session);
		} else {
			session = transactionManager.getSession(transactionID);
			if (session == null) {
				logError("Expected existing transaction for packet of type " + type + ", but found none");
				return;
			}
		}
		if (!command.handle(session, sourceServer, input)) {
			transactionManager.deleteSession(transactionID);
		}
	}

	protected abstract void logError(String msg);

}
