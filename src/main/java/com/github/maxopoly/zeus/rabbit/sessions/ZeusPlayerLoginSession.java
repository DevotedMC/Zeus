package com.github.maxopoly.zeus.rabbit.sessions;

import java.net.InetAddress;
import java.util.UUID;

import com.github.maxopoly.zeus.rabbit.PlayerSpecificPacketSession;
import com.github.maxopoly.zeus.servers.ConnectedServer;

/**
 * Session for tracking the state of a request sent by Bungee when a player
 * first attempts to login by connecting to Bungee
 *
 */
public class ZeusPlayerLoginSession extends PlayerSpecificPacketSession {

	private InetAddress ip;
	private String name;

	public ZeusPlayerLoginSession(ConnectedServer source, String transactionID, UUID player, InetAddress ip,
			String name) {
		super(source, transactionID, player);
		this.ip = ip;
		this.name = name;
	}

	/**
	 * @return Ingame name the player will have
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the ingame name the player will have
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
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
