package com.github.maxopoly.zeus.rabbit;

import org.json.JSONObject;

import com.github.maxopoly.zeus.model.TransactionIdManager;
import com.github.maxopoly.zeus.rabbit.sessions.StandardRequestSession;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public abstract class StandardRequest extends RabbitMessage {

	public StandardRequest(TransactionIdManager idMan, ConnectedServer target) {
		super(idMan.pullNewTicket());
		StandardRequestSession session = new StandardRequestSession(target, getTransactionID(), this::handleReply);
		idMan.putSession(session);
	}

	public abstract void handleReply(JSONObject reply);

}
