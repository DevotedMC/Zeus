package com.github.civcraft.zeus.rabbit.sessions;

import java.util.function.Consumer;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.PacketSession;
import com.github.civcraft.zeus.servers.ConnectedServer;
import com.google.common.base.Preconditions;

public class StandardRequestSession extends PacketSession {

	private Consumer<JSONObject> replyHandler;
	private Runnable timeoutHandler;

	public StandardRequestSession(ConnectedServer source, String transactionID, Consumer<JSONObject> replyHandler,
			Runnable timeoutHandler) {
		super(source, transactionID);
		Preconditions.checkNotNull(replyHandler);
		this.replyHandler = replyHandler;
	}

	public StandardRequestSession(ConnectedServer source, String transactionID, Consumer<JSONObject> replyHandler) {
		this(source, transactionID, replyHandler, null);
	}

	@Override
	public void handleTimeout() {
		if (timeoutHandler != null) {
			timeoutHandler.run();
		}
	}

	public void handleReply(JSONObject json) {
		replyHandler.accept(json);
	}

}
