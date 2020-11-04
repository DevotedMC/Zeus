package com.github.civcraft.zeus.rabbit.incoming.artemis;

import org.json.JSONObject;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.model.ConnectedMapState;
import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.rabbit.incoming.GenericInteractiveRabbitCommand;
import com.github.civcraft.zeus.servers.ArtemisServer;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class ArtemisStartupHandler extends GenericInteractiveRabbitCommand {

	public static final String ID = "artemis_startup";

	@Override
	public void handleRequest(String ticket, ConnectedServer sendingServer, JSONObject data) {
		String server = data.getString("server");
		if (!server.equals(sendingServer.getID())) {
			ZeusMain.getInstance().getLogger()
					.error("Mismatched server id, got: " + server + " and expected: " + sendingServer.getID());
		}
		sendingServer.setActiveConnection(true);
		JSONObject pos = data.getJSONObject("pos");
		ZeusLocation corner = ZeusLocation.parseLocation(pos);
		int xSize = pos.getInt("x_size");
		int zSize = pos.getInt("z_size");
		boolean randomSpawnTarget = pos.getBoolean("random_spawn");
		ZeusMain.getInstance().getServerPlacementManager()
				.registerMapPart(new ConnectedMapState((ArtemisServer) sendingServer, corner, xSize, zSize, randomSpawnTarget));
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
}
