package com.github.maxopoly.zeus.rabbit.sessions;

import java.util.UUID;

import com.github.maxopoly.zeus.servers.ConnectedServer;

public class ZeusPlayerDataTransferSession extends PlayerDataTransferSession {

	public ZeusPlayerDataTransferSession(ConnectedServer source, String transactionID, UUID player) {
		super(source, transactionID, player);
	}
}
