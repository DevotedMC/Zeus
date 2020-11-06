package com.github.civcraft.zeus.rabbit.sessions;

import java.net.InetAddress;
import java.util.UUID;

import com.github.civcraft.zeus.rabbit.PlayerSpecificPacketSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class ZeusPlayerLoginSession extends PlayerSpecificPacketSession {
	
	private InetAddress ip;

	public ZeusPlayerLoginSession(ConnectedServer source, String transactionID, UUID player, InetAddress ip) {
		super(source, transactionID, player);
		this.ip = ip;
	}
	
	/**
	 * @return IP the player is connecting from, may be either IPv4 or IPv6
	 */
	public InetAddress getIP() {
		return ip;
	}

	@Override
	public void handleTimeout() {
		throw new IllegalStateException("Should never be waiting for reply");
	}

}
