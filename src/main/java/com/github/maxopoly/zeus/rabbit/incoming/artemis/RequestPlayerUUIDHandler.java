package com.github.maxopoly.zeus.rabbit.incoming.artemis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.rabbit.DynamicRabbitMessage;
import com.github.maxopoly.zeus.rabbit.common.RequestPlayerUUID;
import com.github.maxopoly.zeus.rabbit.incoming.GenericInteractiveRabbitCommand;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class RequestPlayerUUIDHandler extends GenericInteractiveRabbitCommand {

	@Override
	public void handleRequest(String ticket, ConnectedServer sendingServer, JSONObject data) {
		String name = data.getString("name");
		Map<String, Object> replies = new HashMap<>();
		UUID uuid = ZeusMain.getInstance().getPlayerManager().getUUID(name);
		if (uuid != null) {
			replies.put("uuid", uuid);
		}
		sendReply(sendingServer, new DynamicRabbitMessage(ticket, RequestPlayerUUID.REPLY_ID, replies));
	}

	@Override
	public String getIdentifier() {
		return RequestPlayerUUID.REQUEST_ID;
	}

}
