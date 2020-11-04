package com.github.civcraft.zeus.servers;

import com.google.common.base.Preconditions;

/**
 * Super class for any kind of server connected to Zeus and Zeus itself.
 * Examples include a minecraft server or a bungee instance
 *
 */
public abstract class ConnectedServer {

	private String id;
	private boolean activeConnection;

	public ConnectedServer(String id) {
		Preconditions.checkNotNull(id);
		this.id = id;
		this.activeConnection = false;
	}

	/**
	 * @return Are we actively connected to this server through Rabbit, did it
	 *         respond to our last keep alive. Will be false until initial
	 *         connection has been established
	 */
	public boolean hasActiveConnection() {
		return activeConnection;
	}

	/**
	 * Setter for whether an active rabbit connection exists
	 * 
	 * @return State of rabbit connection
	 */
	public void setActiveConnection(boolean connected) {
		this.activeConnection = connected;
	}

	/**
	 * @return Unique name/id identifying this server
	 */
	public String getID() {
		return id;
	}

	public int hashCode() {
		return id.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof ConnectedServer)) {
			return false;
		}
		return ((ConnectedServer) o).id.equals(id);
	}

}
