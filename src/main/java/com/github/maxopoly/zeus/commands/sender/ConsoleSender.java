package com.github.maxopoly.zeus.commands.sender;

import com.github.maxopoly.zeus.ZeusMain;

public class ConsoleSender implements CommandSender {

	@Override
	public void reply(String msg) {
		ZeusMain.getInstance().getLogger().info(msg);
	}

}
