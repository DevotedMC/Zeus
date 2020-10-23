package com.github.civcraft.zeus.commands.impl;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.commands.ZeusCommand;
import com.github.civcraft.zeus.commands.sender.CommandSender;

public class ShutdownCommand extends ZeusCommand {
	
	public ShutdownCommand() {
		super("stop", "end", "quit", "exit", "shutdown");
	}

	@Override
	public String handle(CommandSender sender, String command) {
		ZeusMain.getInstance().shutDown();
		return "Thank you and good bye";
	}

}
