package com.github.civcraft.zeus.rabbit;

import org.json.JSONObject;

import com.github.civcraft.zeus.model.TransactionIdManager;
import com.github.civcraft.zeus.rabbit.sessions.StandardRequestSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public abstract class StandardRequest extends RabbitMessage {

	public StandardRequest(TransactionIdManager idMan, ConnectedServer target) {
		super(idMan.pullNewTicket());
		StandardRequestSession session = new StandardRequestSession(target, getTransactionID(), this::handleReply);
		idMan.putSession(session);
	}

	public abstract void handleReply(JSONObject reply);

}
