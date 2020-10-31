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
	public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
		String server = data.getString("server");
		if (!server.equals(sendingServer.getID())) {
			ZeusMain.getInstance().getLogger()
					.error("Mismatched server id, got: " + server + " and expected: " + sendingServer.getID());
		}
		JSONObject pos = data.getJSONObject("pos");
		ZeusLocation corner = ZeusLocation.parseLocation(pos);
		int xSize = data.getInt("x_size");
		int zSize = data.getInt("z_size");
		ZeusMain.getInstance().getServerPlacementManager()
				.registerMapPart(new ConnectedMapState((ArtemisServer) sendingServer, corner, xSize, zSize));
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	public boolean createSession() {
		return true;
	}
}
