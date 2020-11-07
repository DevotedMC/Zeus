package com.github.civcraft.zeus.rabbit.sessions;

import java.util.UUID;

import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.rabbit.PlayerSpecificPacketSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class PlayerDataTransferSession extends PlayerSpecificPacketSession {
	private byte [] data;
	private ZeusLocation location;
	private int requestAttempt;

	public PlayerDataTransferSession(ConnectedServer source, String transactionID, UUID player) {
		super(source, transactionID, player);
		this.requestAttempt = 0;
	}
	
	public int getRequestAttempts() {
		return requestAttempt;
	}
	
	public void incrementRequestAttempts() {
		requestAttempt++;
	}
	
	public void setData(byte [] data, ZeusLocation location) {
		this.data = data;
		this.location = location;
	}
	
	public byte [] getData() {
		return data;
	}
	
	public ZeusLocation getLocation() {
		return location;
	}

	@Override
	public void handleTimeout() {
		// TODO Auto-generated method stub
		
	}

}
