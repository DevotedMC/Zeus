package com.github.maxopoly.zeus.servers;

/**
 * A minecraft server client
 *
 */
public class ArtemisServer extends ConnectedServer {

	private boolean nonRabbitUser;
	
	public ArtemisServer(String id, boolean nonRabbitUser) {
		super(id);
		this.nonRabbitUser = nonRabbitUser;
	}
	
	public boolean isNonRabbitUser() {
		return nonRabbitUser;
	}

}
