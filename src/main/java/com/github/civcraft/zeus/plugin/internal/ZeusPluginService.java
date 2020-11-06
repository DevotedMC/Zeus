package com.github.civcraft.zeus.plugin.internal;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.apache.logging.log4j.Logger;

import com.github.civcraft.zeus.commands.ZeusCommand;
import com.github.civcraft.zeus.plugin.ZeusPlugin;

public class ZeusPluginService {

	public static final String PLUGIN_FOLDER = "plugins";
	
	private ServiceLoader<ZeusPlugin> pluginLoader;
	private ServiceLoader<ZeusCommand> commandLoader;
	private URLClassLoader classLoader;

	private Logger logger;

	public ZeusPluginService(Logger logger) {
		this.logger = logger;
		classLoader = addPluginFolderToClassPath();
		reloadJars();
	}

	private URLClassLoader addPluginFolderToClassPath() {
		File dir = new File(PLUGIN_FOLDER);
		if (!dir.exists()) {
			dir.mkdir();
		}
		List<File> jars = new ArrayList<>();
		for (File f : dir.listFiles()) {
			if (f.getName().endsWith(".jar")) {
				jars.add(f);
			}
		}
		List<URL> urlsList = new ArrayList<>();
		for (File file : jars) {
			try {
				urlsList.add(file.toURI().toURL());
			} catch (MalformedURLException e) {
				logger.error("Failed to load jar, invalid path", e);
			}
		}
		return URLClassLoader.newInstance(urlsList.toArray(new URL[] {}),
				Thread.currentThread().getContextClassLoader());
	}

	public synchronized List<ZeusPlugin> getAvailablePlugins() {
		List<ZeusPlugin> plugins = new ArrayList<>();
		Iterator<ZeusPlugin> iter = pluginLoader.iterator();
		while (iter.hasNext()) {
			try {
				plugins.add(iter.next());
			} catch (ServiceConfigurationError e) {
				logger.warn("Failed to load a plugin, here's some debug info for its dev: ", e);
			}
		}
		return plugins;
	}
	
	public synchronized List<ZeusCommand> getAvailableCommands() {
		List<ZeusCommand> commands = new ArrayList<>();
		Iterator<ZeusCommand> iter = commandLoader.iterator();
		while (iter.hasNext()) {
			try {
				commands.add(iter.next());
			} catch (ServiceConfigurationError e) {
				logger.warn("Failed to load a command, here's some debug info for its dev: ", e);
			}
		}
		return commands;
	}

	public synchronized void reloadJars() {
		this.pluginLoader = ServiceLoader.load(ZeusPlugin.class, classLoader);
		this.commandLoader = ServiceLoader.load(ZeusCommand.class, classLoader);
	}
}
