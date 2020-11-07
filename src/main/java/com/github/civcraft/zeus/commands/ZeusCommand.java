package com.github.civcraft.zeus.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;

import com.github.civcraft.zeus.commands.sender.CommandSender;

public abstract class ZeusCommand {

	private static final Pattern validCommands = Pattern.compile("[a-zA-Z0-9]+");

	private String id;
	private List<String> altIdentifiers;
	private int minArgs;
	private int maxArgs;

	boolean setupInternals(Logger logger) {
		ZCommand annot = getPluginAnnotation();
		if (!validCommands.matcher(annot.id()).matches()) {
			logger.warn(String.format("Main command identifier %s is not valid", annot.id()));
			return false;
		}
		id = annot.id();
		if (annot.altIds() != null && !annot.altIds().isEmpty()) {
			altIdentifiers = Arrays.asList(annot.altIds().split(" "));
			for (String altId : altIdentifiers) {
				if (!validCommands.matcher(altId).matches()) {
					logger.warn("Alternative identifier " + altId + " in command " + id + " was not valid");
					return false;
				}
			}
		} else {
			altIdentifiers = Collections.emptyList();
		}
		if (annot.minArgs() != ZCommand.DEFAULT_ARG_NUM) {
			minArgs = annot.minArgs();
		} else {
			minArgs = annot.args();
		}
		if (minArgs < 0) {
			logger.warn("Minimum amount of arguments can not be less than 0");
			return false;
		}
		if (annot.maxArgs() != ZCommand.DEFAULT_ARG_NUM) {
			maxArgs = annot.maxArgs();
		} else {
			maxArgs = annot.args();
		}
		if (maxArgs < minArgs) {
			logger.warn("Maximum amount of arguments can not be less than the minimum amount");
			return false;
		}
		return true;
	}

	private ZCommand getPluginAnnotation() {
		Class<? extends ZeusCommand> pluginClass = this.getClass();
		return pluginClass.getAnnotation(ZCommand.class);
	}

	/**
	 * @return Alternative commands, which will also execute this command
	 */
	List<String> getAlternativeIdentifiers() {
		return Collections.unmodifiableList(altIdentifiers);
	}

	/**
	 * @return The actual string entered to run this command
	 */
	String getIdentifier() {
		return id;
	}

	/**
	 * @return Minimum amount of arguments required by this command
	 */
	int getMinArgs() {
		return minArgs;
	}

	/**
	 * @return Maximum amount of arguments accepted by this command
	 */
	int getMaxArgs() {
		return maxArgs;
	}

	public abstract String handle(CommandSender sender, String command);

}
