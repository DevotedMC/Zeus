package com.github.civcraft.zeus.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.github.civcraft.zeus.commands.impl.ShutdownCommand;
import com.github.civcraft.zeus.commands.sender.CommandSender;

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
	
	private void registerCommand(ZeusCommand command) {
		commands.put(command.getIdentifier().toLowerCase(), command);
		for(String alt : command.getAlternativeIdentifiers()) {
			commands.putIfAbsent(alt.toLowerCase(), command);
		}
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
