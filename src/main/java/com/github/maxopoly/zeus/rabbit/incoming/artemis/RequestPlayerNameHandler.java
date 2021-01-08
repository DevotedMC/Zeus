package com.github.maxopoly.zeus.rabbit.incoming.artemis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.rabbit.DynamicRabbitMessage;
import com.github.maxopoly.zeus.rabbit.common.RequestPlayerName;
import com.github.maxopoly.zeus.rabbit.incoming.GenericInteractiveRabbitCommand;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class RequestPlayerNameHandler extends GenericInteractiveRabbitCommand {

	@Override
	public void handleRequest(String ticket, ConnectedServer sendingServer, JSONObject data) {
		UUID uuid = UUID.fromString(data.getString("uuid"));
		Map<String, Object> replies = new HashMap<>();
		String name = ZeusMain.getInstance().getPlayerManager().getName(uuid);
		if (name != null) {
			replies.put("name", name);
		}
		sendReply(sendingServer, new DynamicRabbitMessage(ticket, RequestPlayerName.REPLY_ID, replies));
	}

	@Override
	public String getIdentifier() {
		return RequestPlayerName.REQUEST_ID;
	}

}
