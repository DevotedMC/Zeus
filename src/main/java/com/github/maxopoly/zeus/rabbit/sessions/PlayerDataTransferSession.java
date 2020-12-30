package com.github.maxopoly.zeus.rabbit.sessions;

import java.util.UUID;

import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.rabbit.PlayerSpecificPacketSession;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class PlayerDataTransferSession extends PlayerSpecificPacketSession {
	private byte[] data;
	private ZeusLocation location;

	public PlayerDataTransferSession(ConnectedServer source, String transactionID, UUID player) {
		super(source, transactionID, player);
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public void setLocation(ZeusLocation location) {
		this.location = location;
	}

	public byte[] getData() {
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
