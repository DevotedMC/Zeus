package com.github.civcraft.zeus.rabbit.sessions;

import java.util.UUID;

import com.github.civcraft.zeus.rabbit.PacketSession;
import com.github.civcraft.zeus.servers.ArtemisServer;
import com.github.civcraft.zeus.servers.ChildServer;

public class PlayerTransferSession extends PacketSession {

	private UUID player;
	private ArtemisServer sourceServer;
	private ArtemisServer targetServer;
	
	public PlayerTransferSession(ChildServer source, String transactionID, UUID player) {
		super(source, transactionID);
		this.player = player;
	}
	
	public void setSourceServer(ArtemisServer server) {
		this.sourceServer = server;
	}
	
	public void setTargetServer(ArtemisServer server) {
		this.targetServer = server;
	}
	
	public ArtemisServer getSourceServer() {
		return sourceServer;
	}
	
	public ArtemisServer getTargetServer() {
		return targetServer;
	}
	
	public UUID getPlayer() {
		return player;
	}

	@Override
	public void handleTimeout() {
		// TODO Auto-generated method stub
		
	}

}
