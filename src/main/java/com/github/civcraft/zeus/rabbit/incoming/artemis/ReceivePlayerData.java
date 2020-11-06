package com.github.civcraft.zeus.rabbit.incoming.artemis;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.plugin.event.events.ReceivePlayerDataEvent;
import com.github.civcraft.zeus.rabbit.incoming.StaticRabbitCommand;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.SendPlayerData;
import com.github.civcraft.zeus.servers.ArtemisServer;
import com.github.civcraft.zeus.servers.ConnectedServer;
import com.github.civcraft.zeus.util.Base64Encoder;

public class ReceivePlayerData extends StaticRabbitCommand {
	@Override
	public String getIdentifier() {
		return SendPlayerData.ID;
	}

	@Override
	public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
		byte[] playerData;
		String serializedData = data.getString("data");
		playerData = Base64Encoder.decode(serializedData);
		ZeusLocation location = ZeusLocation.parseLocation(data.getJSONObject("loc"));
		UUID player = UUID.fromString(data.getString("player"));
		ZeusMain.getInstance().getEventManager()
				.broadcast(new ReceivePlayerDataEvent(player, playerData, location, (ArtemisServer) sendingServer));
		ZeusMain.getInstance().getDAO().savePlayerNBT(player, playerData, location, sendingServer);
	}

}
