package com.github.maxopoly.zeus.rabbit.sessions;

import java.util.UUID;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.model.TransferRejectionReason;
import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.rabbit.PlayerSpecificPacketSession;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.RejectPlayerTransfer;
import com.github.maxopoly.zeus.servers.ArtemisServer;
import com.github.maxopoly.zeus.servers.ConnectedServer;
import com.google.common.base.Preconditions;

public class PlayerTransferSession extends PlayerSpecificPacketSession {

	private ArtemisServer sourceServer;
	private ArtemisServer targetServer;
	private ZeusLocation location;

	public PlayerTransferSession(ConnectedServer source, String transactionID, UUID player, ZeusLocation location) {
		super(source, transactionID, player);
		Preconditions.checkNotNull(location);
		this.location = location;
	}

	public ZeusLocation getLocation() {
		return location;
	}

	public void setSourceServer(ArtemisServer server) {
		this.sourceServer = server;
	}

	public void setTargetServer(ArtemisServer server) {
		this.targetServer = server;
	}

	public ArtemisServer getSourceServer() {
		return sourceServer;
	}

	public ArtemisServer getTargetServer() {
		return targetServer;
	}

	@Override
	protected long getExpirationTime() {
		return 7_000L;
	}

	@Override
	public void handleTimeout() {
		ZeusMain.getInstance().getRabbitGateway().sendMessage(sourceServer,
				new RejectPlayerTransfer(getTransactionID(), TransferRejectionReason.TARGET_DOWN));
	}

}
