package com.github.maxopoly.zeus.model;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.CachePlayerName;
import com.github.maxopoly.zeus.servers.ArtemisServer;

public class PlayerNameKnowledgeTracker {

	private Map<ArtemisServer, Set<UUID>> knownPlayers;

	public PlayerNameKnowledgeTracker() {
		this.knownPlayers = new ConcurrentHashMap<>();
	}

	public void ensureIsCached(UUID player, ArtemisServer server) {
		Set<UUID> localPlayers = knownPlayers.computeIfAbsent(server,
				t -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
		if (localPlayers.contains(player)) {
			return;
		}
		localPlayers.add(player);
		String name = ZeusMain.getInstance().getPlayerManager().getName(player);
		ZeusMain.getInstance().getRabbitGateway().sendMessage(server,
				new CachePlayerName(ZeusMain.getInstance().getTransactionIdManager().pullNewTicket(), player, name));

	}

	public void reset(ArtemisServer server) {
		knownPlayers.remove(server);
	}

}
