package com.github.civcraft.zeus.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import com.github.civcraft.zeus.commands.impl.ShutdownCommand;
import com.github.civcraft.zeus.commands.sender.CommandSender;

import vg.civcraft.mc.civmodcore.command.CivConfigAnnotationProcessor;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

public class ZeusCommandHandler {
	
	private Map<String, ZeusCommand> commands;
	private Logger logger;
	
	public ZeusCommandHandler(Logger logger) {
		this.logger = logger;
		this.commands = new HashMap<>();
		registerCommands();
	}
	
	private void registerCommands() {
		registerCommand(new ShutdownCommand());
	}
	
	public void registerCommand(ZeusCommand command) {
		if (!command.setupInternals(logger)) {
			logger.warn("Failed to load command " + command.getClass());
			return;
		}
		commands.put(command.getIdentifier().toLowerCase(), command);
		for(String alt : command.getAlternativeIdentifiers()) {
			commands.putIfAbsent(alt.toLowerCase(), command);
		}
	}
	
	private void loadAll() {
		File file = getPluginJar();
		if (file == null) {
			return;
		}
		@SuppressWarnings("deprecation")
		JavaPluginLoader pluginLoader = new JavaPluginLoader(Bukkit.getServer());
		PluginDescriptionFile pluginYml;
		try {
			pluginYml = pluginLoader.getPluginDescription(file);
		} catch (InvalidDescriptionException e1) {
			plugin.getLogger().severe("Plugin " + plugin.getName() + " had invalid plugin.yml");
			return;
		}
		try (JarFile jar = new JarFile(file)) {
			JarEntry entry = jar.getJarEntry(CivConfigAnnotationProcessor.fileLocation);
			if (entry == null) {
				// doesn't exist, that's fine
				return;
			}
			try (InputStream stream = jar.getInputStream(entry);
					InputStreamReader reader = new InputStreamReader(stream);
					BufferedReader buffer = new BufferedReader(reader)) {
				String line;
				while ((line = buffer.readLine()) != null) {
					loadCommand(line, pluginYml);
				}
			}
		} catch (IOException e) {
			plugin.getLogger().severe("Failed to load plugin.yml: " + e.toString());
		}
	}

	private File getPluginJar() {
		try {
			Method method = JavaPlugin.class.getDeclaredMethod("getFile");
			method.setAccessible(true);
			return (File) method.invoke(plugin);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			plugin.getLogger().severe("Failed to retrieve plugin file: " + e.toString());
			return null;
		}
	}

	private void loadCommand(String classPath, PluginDescriptionFile pluginYml) {
		Class<?> commandClass;
		try {
			commandClass = Class.forName(classPath);
		} catch (ClassNotFoundException e) {
			plugin.getLogger().warning("Attempted to load command " + classPath + ", but it could not be found");
			return;
		}
		StandaloneCommand command;
		try {
			command = (StandaloneCommand) commandClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			plugin.getLogger().warning("Error occured when loading command " + classPath + ": " + e.toString());
			return;
		}
		Object commandSection = pluginYml.getCommands().get(command.getIdentifier().toLowerCase());
		if (commandSection == null) {
			plugin.getLogger().warning("No command with the identifier " + command.getIdentifier()
					+ " could be found in the plugin.yml. Command will be unavailable");
			return;
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> commandMap = (Map<String, Object>) commandSection;
		command.setRateLimiter(
				parseRateLimiter(command.getIdentifier() + "-ratelimit", commandMap.get("rate-limiter")));
		command.setTabCompletionRateLimiter(
				parseRateLimiter(command.getIdentifier() + "-tabratelimit", commandMap.get("tab-rate-limiter")));
		Boolean playerOnly = attemptBoolean(commandMap.get("player-only"));
		if (playerOnly != null) {
			command.setSenderMustBePlayer(playerOnly);
		}
		Boolean consoleOnly = attemptBoolean(commandMap.get("console-only"));
		if (consoleOnly != null) {
			command.setSenderMustBeConsole(consoleOnly);
			if (consoleOnly && playerOnly != null && playerOnly) {
				plugin.getLogger().severe("Command " + command.getIdentifier()
						+ " is simultaneously console only and player only. It can not be run");
			}
		}
		Integer minArgs = attemptInteger(commandMap.get("min-args"));
		if (minArgs != null) {
			command.setMinArgs(minArgs);
		}
		Integer maxArgs = attemptInteger(commandMap.get("max-args"));
		if (maxArgs != null) {
			command.setMaxArgs(maxArgs);
		}
		this.commands.put(command.getIdentifier().toLowerCase(), command);
	}
	
	public void handleInput(CommandSender sender, String msg) {
		int index = msg.indexOf(' ');
		String identifier;
		if (index == -1) {
			identifier = msg;
		}
		else {
			identifier = msg.substring(0, index);
		}
		ZeusCommand command = commands.get(identifier.toLowerCase());
		if (command == null) {
			sender.reply(String.format("The command '%s' does not exist", identifier));
			return;
		}
		String args;
		if (index == -1) {
			args = "";
		}
		else {
			args = msg.substring(index, msg.length());
		}
		command.handle(sender, args);
		
	}

}
