package com.github.maxopoly.zeus.rabbit.common;

import java.util.UUID;
import java.util.function.Consumer;

import org.json.JSONObject;

import com.github.maxopoly.zeus.model.TransactionIdManager;
import com.github.maxopoly.zeus.rabbit.StandardRequest;
import com.github.maxopoly.zeus.servers.ConnectedServer;
import com.google.common.base.Preconditions;

public class RequestPlayerName extends StandardRequest {
	
	public static final String REQUEST_ID = "get_player_name";
	public static final String REPLY_ID = "reply_get_player_name";
	
	private UUID uuid;
	private Consumer<String> replyHandler;

	public RequestPlayerName(TransactionIdManager idMan, ConnectedServer target, UUID uuid, Consumer <String> replyHandler) {
		super(idMan, target);
		Preconditions.checkNotNull(uuid);
		Preconditions.checkNotNull(replyHandler);
		this.uuid = uuid;
		this.replyHandler = replyHandler;
	}

	@Override
	public void handleReply(JSONObject reply) {
		String name = reply.optString("name");
		replyHandler.accept(name);
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("uuid", uuid);
	}

	@Override
	public String getIdentifier() {
		return REQUEST_ID;
	}

}
