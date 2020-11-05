package com.github.civcraft.zeus.plugin.event;

public class CancellableEvent implements ZeusEvent {
	
	private boolean isCancelled = false;
	
	public boolean isCancelled() {
		return isCancelled;
	}
	
	public void setCancelled(boolean cancelled) {
		this.isCancelled = cancelled;
	}

}
