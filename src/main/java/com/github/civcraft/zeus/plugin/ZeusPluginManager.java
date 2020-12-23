package com.github.civcraft.zeus.plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.plugin.event.events.PluginDisableEvent;
import com.github.civcraft.zeus.plugin.event.events.PluginEnableEvent;
import com.github.civcraft.zeus.plugin.internal.ZeusPluginService;

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

	public void enableAllPlugins() {
		Map<String, ZeusPlugin> todoPlugins = new HashMap<>(plugins);
		Map<String, ZeusPlugin> finishedPlugins = new HashMap<>(plugins);
		List<ZeusPlugin> enableOrder = new ArrayList<>();
		while (!todoPlugins.isEmpty()) {
			Iterator<Entry<String, ZeusPlugin>> iter = todoPlugins.entrySet().iterator();
			boolean found = false;
			while (iter.hasNext()) {
				ZeusPlugin currentPlugin = iter.next().getValue();
				boolean hasUnhandledDependency = false;
				for (String dependency : currentPlugin.getDependencies()) {
					if (todoPlugins.containsKey(dependency)) {
						hasUnhandledDependency = true;
						break;
					}
					if (!finishedPlugins.containsKey(dependency)) {
						logger.error("Can not enable plugin " + currentPlugin.getName() + ", because its dependency "
								+ dependency + " was not available");
						iter.remove();
						hasUnhandledDependency = true;
					}
				}
				if (hasUnhandledDependency) {
					continue;
				}
				// all dependencies available
				iter.remove();
				finishedPlugins.put(currentPlugin.getName(), currentPlugin);
				enableOrder.add(currentPlugin);
				found = true;
				break;
			}
			if (!found) {
				logger.error(
						"Cyclic dependency in plugins detected, unresolved plugin set which will not be enabled is: "
								+ todoPlugins);
				break;
			}
		}
		for (ZeusPlugin plugin : enableOrder) {
			startPlugin(plugin);
		}
	}

	/**
	 * Starts a plugin
	 *
	 * @param pluginName Plugin to start
	 */
	public void startPlugin(ZeusPlugin plugin) {
		logger.info("Enabling plugin " + plugin.getName() + ":" + plugin.getVersion());
		ZeusMain.getInstance().getEventManager().broadcast(new PluginEnableEvent(plugin));
		if (plugin.enable(logger, new File(mainServerFolder, ZeusPluginService.PLUGIN_FOLDER))) {
			activePlugins.add(plugin);
		}
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
		ZeusLoad pluginAnnotation = pluginClass.getAnnotation(ZeusLoad.class);
		if (pluginAnnotation == null) {
			logger.warn("Plugin " + plugin.getClass().getName() + " had no AngeliaLoad annotation, it was ignored");
			return;
		}
		Constructor<?> constr = pluginClass.getConstructors()[0];
		if (constr.getParameterCount() != 0) {
			logger.warn("Plugin " + plugin.getClass().getName() + " had no default constructor, it was ignored");
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
		while (!activePlugins.isEmpty()) {
			stopPlugin(activePlugins.iterator().next());
		}
	}

	private void stopPlugin(ZeusPlugin plugin) {
		logger.info("Disabling plugin " + plugin.getName() + ":" + plugin.getVersion());
		try {
			ZeusMain.getInstance().getEventManager().broadcast(new PluginDisableEvent(plugin));
			plugin.disable();
		} catch (Exception e) {
			logger.warn("Failed to stop plugin", e);
		}
		activePlugins.remove(plugin);
	}

}
