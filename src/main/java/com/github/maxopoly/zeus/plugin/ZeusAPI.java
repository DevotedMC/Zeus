package com.github.maxopoly.zeus.plugin;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.rabbit.incoming.RabbitRequest;

public class ZeusAPI {

	private ZeusAPI() {
	}

	public static void registerRabbitCommand(RabbitRequest handler) {
		ZeusMain.getInstance().getRabbitGateway().getInputHandler().registerCommand(handler);
	}
}
