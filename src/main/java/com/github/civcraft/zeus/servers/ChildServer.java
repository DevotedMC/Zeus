package com.github.civcraft.zeus.servers;

import com.github.civcraft.zeus.TransactionIdState;
import com.google.common.base.Preconditions;

/**
 * Super class for any kind of client connected to Zeus. Examples include a
 * minecraft server or a bungee instance
 *
 */
public abstract class ChildServer {

	private String id;
	private TransactionIdState transactions;

	public ChildServer(String id) {
		Preconditions.checkNotNull(id);
		this.id = id;
		this.transactions = new TransactionIdState();
	}
	
	public TransactionIdState getTransactionState() {
		return transactions;
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
		if (!(o instanceof ChildServer)) {
			return false;
		}
		return ((ChildServer) o).id.equals(id);
	}

}
