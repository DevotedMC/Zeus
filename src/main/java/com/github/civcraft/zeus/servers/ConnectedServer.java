package com.github.civcraft.zeus.servers;

import com.github.civcraft.zeus.model.TransactionIdManager;
import com.google.common.base.Preconditions;

/**
 * Super class for any kind of server connected to Zeus and Zeus itself. Examples include a
 * minecraft server or a bungee instance
 *
 */
public abstract class ConnectedServer {

	private String id;

	public ConnectedServer(String id) {
		Preconditions.checkNotNull(id);
		this.id = id;
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
