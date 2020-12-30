package com.github.maxopoly.zeus.commands.impl;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.commands.ZCommand;
import com.github.maxopoly.zeus.commands.ZeusCommand;
import com.github.maxopoly.zeus.commands.sender.CommandSender;

@ZCommand(description = "Initiates Zeus shutdown process", altIds = "end shutdown exit", id = "stop")
public class ShutdownCommand extends ZeusCommand {

	@Override
	public String handle(CommandSender sender, String command) {
		ZeusMain.getInstance().shutDown();
		return "Thank you and good bye";
	}

}
