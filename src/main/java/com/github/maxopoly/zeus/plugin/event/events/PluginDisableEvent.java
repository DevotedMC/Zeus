package com.github.maxopoly.zeus.plugin.event.events;

import com.github.maxopoly.zeus.plugin.ZeusPlugin;
import com.github.maxopoly.zeus.plugin.event.ZeusEvent;

/**
 * Called when an actively running plugin is disabled in the plugin manager
 * before the plugin is actually disabled
 *
 */
public class PluginDisableEvent implements ZeusEvent {

	private ZeusPlugin plugin;

	public PluginDisableEvent(ZeusPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * @return Plugin being disabled
	 */
	public ZeusPlugin getPlugin() {
		return plugin;
	}

}
