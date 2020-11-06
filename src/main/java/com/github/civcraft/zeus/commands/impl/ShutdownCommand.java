package com.github.civcraft.zeus.commands.impl;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.commands.ZCommand;
import com.github.civcraft.zeus.commands.ZeusCommand;
import com.github.civcraft.zeus.commands.sender.CommandSender;

@ZCommand(description = "Initiates Zeus shutdown process", altIds = "end shutdown exit", id = "stop")
public class ShutdownCommand extends ZeusCommand {

	@Override
	public String handle(CommandSender sender, String command) {
		ZeusMain.getInstance().shutDown();
		return "Thank you and good bye";
	}

}
