package com.github.maxopoly.zeus.rabbit.incoming.apollo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.model.GlobalPlayerData;
import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.plugin.event.events.PlayerInitialLoginEvent;
import com.github.maxopoly.zeus.plugin.event.events.PlayerJoinServerEvent;
import com.github.maxopoly.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.outgoing.apollo.ConfirmInitialPlayerLogin;
import com.github.maxopoly.zeus.rabbit.outgoing.apollo.RejectPlayerInitialLogin;
import com.github.maxopoly.zeus.rabbit.sessions.ZeusPlayerLoginSession;
import com.github.maxopoly.zeus.servers.ApolloServer;
import com.github.maxopoly.zeus.servers.ArtemisServer;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class PlayerLoginRequest extends InteractiveRabbitCommand<ZeusPlayerLoginSession> {

	public static final String ID = "initial_login_request";

	@Override
	public boolean handleRequest(ZeusPlayerLoginSession connState, ConnectedServer sendingServer, JSONObject data) {
		ZeusLocation location = ZeusMain.getInstance().getDAO().getLocation(connState.getPlayer());
		ArtemisServer target = ZeusMain.getInstance().getServerPlacementManager().getTargetServer(location, true);
		boolean whiteListed = ZeusMain.getInstance().getWhitelistManager().canEnterServer(connState.getPlayer(),
				target);
		if (!whiteListed) {
			sendReply(connState.getServerTalkedTo(),
					new RejectPlayerInitialLogin(connState.getTransactionID(), "Insufficient whitelist level"));
			return false;
		}
		PlayerInitialLoginEvent loginEvent = new PlayerInitialLoginEvent(connState.getPlayer(), connState.getIP(),
				target, location, connState.getName());
		ZeusMain.getInstance().getEventManager().broadcast(loginEvent);
		if (loginEvent.isCancelled()) {
			String msg;
			if (loginEvent.getDenyMessage() != null) {
				msg = loginEvent.getDenyMessage();
			} else {
				msg = "Login denied";
			}
			sendReply(connState.getServerTalkedTo(), new RejectPlayerInitialLogin(connState.getTransactionID(), msg));
			return false;
		}
		if (target == null) {
			sendReply(connState.getServerTalkedTo(),
					new RejectPlayerInitialLogin(connState.getTransactionID(), "No target found"));
			return false;
		}
		if (!target.isNonRabbitUser() && !target.hasActiveConnection()) {
			sendReply(connState.getServerTalkedTo(),
					new RejectPlayerInitialLogin(connState.getTransactionID(), "Target server is offline"));
			return false;
		}
		String currentMojangName = loginEvent.getPlayerName();
		String cachedName = ZeusMain.getInstance().getPlayerManager().getName(loginEvent.getPlayer());
		if (cachedName == null) {
			ZeusMain.getInstance().getDAO().setPlayerName(loginEvent.getPlayer(), currentMojangName);
			cachedName = currentMojangName;
		}
		else {
			if (ZeusMain.getInstance().getConfigManager().allowNameChanges()) {
				if (!cachedName.equals(currentMojangName)) {
					ZeusMain.getInstance().getDAO().setPlayerName(loginEvent.getPlayer(), currentMojangName);
					cachedName = currentMojangName;
				}
			}
		}
		GlobalPlayerData gpdata = new GlobalPlayerData(connState.getPlayer(), cachedName, (ApolloServer) sendingServer);
		ZeusMain.getInstance().getPlayerManager().addPlayer(gpdata);
		ZeusMain.getInstance().getEventManager().broadcast(new PlayerJoinServerEvent(gpdata, null, target));
		sendReply(connState.getServerTalkedTo(),
				new ConfirmInitialPlayerLogin(connState.getTransactionID(), target.getID(), cachedName));
		return false;
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	public boolean createSession() {
		return true;
	}

	@Override
	protected ZeusPlayerLoginSession getFreshSession(ConnectedServer source, String transactionID, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		String name = data.getString("name");
		InetAddress ip;
		try {
			ip = InetAddress.getByName(data.getString("ip"));
		} catch (UnknownHostException e) {
			ZeusMain.getInstance().getLogger().error("Could not parse ip", e);
			ip = null;
		}
		return new ZeusPlayerLoginSession(source, transactionID, player, ip, name);
	}

}
