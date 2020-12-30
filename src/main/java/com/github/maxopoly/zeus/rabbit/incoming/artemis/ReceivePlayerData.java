package com.github.maxopoly.zeus.rabbit.incoming.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.plugin.event.events.ReceivePlayerDataEvent;
import com.github.maxopoly.zeus.rabbit.common.ReplyReceivedPlayerData;
import com.github.maxopoly.zeus.rabbit.incoming.GenericInteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.SendPlayerData;
import com.github.maxopoly.zeus.servers.ArtemisServer;
import com.github.maxopoly.zeus.servers.ConnectedServer;
import com.github.maxopoly.zeus.util.Base64Encoder;

public class ReceivePlayerData extends GenericInteractiveRabbitCommand {
	
	@Override
	public String getIdentifier() {
		return SendPlayerData.ID;
	}

	@Override
	public void handleRequest(String ticket, ConnectedServer sendingServer, JSONObject data) {
		byte[] playerData;
		String serializedData = data.getString("data");
		playerData = Base64Encoder.decode(serializedData);
		ZeusLocation location = ZeusLocation.parseLocation(data.getJSONObject("loc"));
		UUID player = UUID.fromString(data.getString("player"));
		ZeusMain.getInstance().getEventManager()
				.broadcast(new ReceivePlayerDataEvent(player, playerData, location, (ArtemisServer) sendingServer));
		boolean accepted = ZeusMain.getInstance().getDAO().savePlayerNBT(player, playerData, location, sendingServer);
		sendReply(sendingServer, new ReplyReceivedPlayerData(ticket, accepted));
	}

}
