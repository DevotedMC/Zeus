package com.github.civcraft.zeus.commands;

import java.util.List;

import org.apache.logging.log4j.Logger;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.commands.sender.CommandSender;

public abstract class ZeusCommand {
	
	private String identifier;
	private String[] alternativeIdentifiers;
	protected Logger logger;

	public ZeusCommand(String identifier, String... alt) {
		this.identifier = identifier;
		this.alternativeIdentifiers = alt;
		this.logger = ZeusMain.getInstance().getLogger();
	}

	/**
	 * @return Alternative commands, which will also execute this
	 */
	public String[] getAlternativeIdentifiers() {
		return alternativeIdentifiers;
	}

	/**
	 * @return The actual string entered to run this command
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	public abstract String handle(CommandSender sender, String command);

}
