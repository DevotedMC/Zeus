package com.github.civcraft.zeus.plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;

public class ZeusPluginManager {

	private ZeusPluginService pluginService;
	private Map<String, ZeusPlugin> plugins;
	private Set<ZeusPlugin> activePlugins;
	private Logger logger;
	private File mainServerFolder;

	public ZeusPluginManager(Logger logger, File mainServerFolder) {
		this.plugins = new HashMap<>();
		this.activePlugins = new HashSet<>();
		this.logger = logger;
		this.mainServerFolder = mainServerFolder;
		this.pluginService = new ZeusPluginService(logger);
		reloadPlugins();
	}

	/**
	 * Starts a plugin and returns the plugin started
	 *
	 * @param pluginName Name of the plugin to start
	 * @param args       Arguments to pass to the plugin on startup
	 * @return Created plugin instance
	 */
	public ZeusPlugin startPlugin(String pluginName) {
		ZeusPlugin plugin = getPlugin(pluginName);
		if (plugin == null) {
			logger.warn(String.format("Plugin %s did not exist", pluginName));
			return null;
		}
		plugin.enable(logger, new File(mainServerFolder, ZeusPluginService.PLUGIN_FOLDER));
		return plugin;
	}

	/**
	 * Retrieves a plugin by its name, case-insensitive
	 * 
	 * @param name Name of the plugin to retrieve
	 * @return Plugin with the given name or null if no such plugin exists
	 */
	public ZeusPlugin getPlugin(String name) {
		return plugins.get(name.toLowerCase());
	}

	private void registerPlugin(ZeusPlugin plugin) {
		Class<? extends ZeusPlugin> pluginClass = plugin.getClass();
		ZeusPlugin pluginAnnotation = pluginClass.getAnnotation(ZeusLoad.class);
		if (pluginAnnotation == null) {
			logger
					.warn("Plugin " + plugin.getClass().getName() + " had no AngeliaLoad annotation, it was ignored");
			return;
		}
		Constructor<?> constr = pluginClass.getConstructors()[0];
		if (constr.getParameterCount() != 0) {
			logger
					.warn("Plugin " + plugin.getClass().getName() + " had no default constructor, it was ignored");
			return;
		}
		constr.setAccessible(true);
		String name = pluginAnnotation.name();
		if (plugins.containsKey(name.toLowerCase())) {
			logger.warn("Plugin " + name + " was already registered, did not register again");
			return;
		}
		logger.info("Registering plugin " + name);
		plugins.put(name.toLowerCase(), plugin);
	}

	public void reloadPlugins() {
		pluginService.reloadJars();
		for (ZeusPlugin plugin : pluginService.getAvailablePlugins()) {
			registerPlugin(plugin);
		}
		logger.info("Loaded a total of " + plugins.size() + " plugin(s)");
	}

	/**
	 * Stops all plugins
	 */
	public void shutDown() {
		while (!runningPlugins.isEmpty()) {
			stopPlugin(runningPlugins.get(0));
		}
	}

	/**
	 * Finishes the plugin with the given name
	 *
	 * @param name Name of the plugin to stop
	 * @return Whether a plugin was stopped
	 */
	public boolean stopPlugin(String name) {
		name = name.toLowerCase();
		Iterator<AngeliaPlugin> iterator = runningPlugins.iterator();
		while (iterator.hasNext()) {
			AngeliaPlugin plugin = iterator.next();
			if (plugin.getName().toLowerCase().equals(name)) {
				stopPlugin(plugin);
				return true;
			}
		}
		return false;
	}

	private void stopPlugin(AngeliaPlugin plugin) {
		connection.getLogger().info("Stopping plugin " + plugin.getName());
		try {
			plugin.stop();
		} catch (Exception e) {
			connection.getLogger().warn("Failed to stop plugin", e);
		}
		runningPlugins.remove(plugin);
	}

}
