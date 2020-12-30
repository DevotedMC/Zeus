package com.github.maxopoly.zeus.rabbit.common;

import java.util.UUID;
import java.util.function.Consumer;

import org.json.JSONObject;

import com.github.maxopoly.zeus.model.TransactionIdManager;
import com.github.maxopoly.zeus.rabbit.StandardRequest;
import com.github.maxopoly.zeus.servers.ConnectedServer;
import com.google.common.base.Preconditions;

public class RequestPlayerUUID extends StandardRequest {
	
	public static final String REQUEST_ID = "get_player_uuid";
	public static final String REPLY_ID = "reply_get_player_uuid";
	
	private String name;
	private Consumer<UUID> replyHandler;

	public RequestPlayerUUID(TransactionIdManager idMan, ConnectedServer target, String name, Consumer <UUID> replyHandler) {
		super(idMan, target);
		Preconditions.checkNotNull(name);
		Preconditions.checkNotNull(replyHandler);
		this.name = name;
		this.replyHandler = replyHandler;
	}

	@Override
	public void handleReply(JSONObject reply) {
		UUID uuid;
		if (reply.has("uuid")) {
			uuid = UUID.fromString(reply.getString("uuid"));
		} else {
			uuid = null;
		}
		replyHandler.accept(uuid);
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("name", name);
	}

	@Override
	public String getIdentifier() {
		return REQUEST_ID;
	}

}
