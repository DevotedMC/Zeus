package com.github.civcraft.zeus.plugin;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.rabbit.incoming.RabbitRequest;

public class ZeusAPI {

	private ZeusAPI() {
	}

	public static void registerRabbitCommand(RabbitRequest handler) {
		ZeusMain.getInstance().getRabbitGateway().getInputHandler().registerCommand(handler);
	}
}
