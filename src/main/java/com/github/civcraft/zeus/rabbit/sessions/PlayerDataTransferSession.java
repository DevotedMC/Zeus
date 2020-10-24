package com.github.civcraft.zeus.rabbit.sessions;

import java.util.UUID;

import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.rabbit.PacketSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class PlayerDataTransferSession extends PacketSession {
	
	private UUID player;
	private byte [] data;
	private ZeusLocation location;

	public PlayerDataTransferSession(ConnectedServer source, String transactionID, UUID player) {
		super(source, transactionID);
		this.player = player;
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
