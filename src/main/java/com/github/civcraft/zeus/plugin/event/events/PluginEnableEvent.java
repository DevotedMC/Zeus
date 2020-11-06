package com.github.civcraft.zeus.plugin.event.events;

import com.github.civcraft.zeus.plugin.ZeusPlugin;
import com.github.civcraft.zeus.plugin.event.ZeusEvent;

/**
 * Called when a non-running plugin is enabled in the plugin manager, for
 * example on initial startup. This event is called before the plugin is
 * actually enabled
 *
 */
public class PluginEnableEvent implements ZeusEvent {

	private ZeusPlugin plugin;

	public PluginEnableEvent(ZeusPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * @return Plugin being enabled
	 */
	public ZeusPlugin getPlugin() {
		return plugin;
	}

}