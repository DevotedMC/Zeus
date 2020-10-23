package com.github.civcraft.zeus.commands.sender;

import com.github.civcraft.zeus.ZeusMain;

public class ConsoleSender implements CommandSender {

	@Override
	public void reply(String msg) {
		ZeusMain.getInstance().getLogger().info(msg);
	}

}
