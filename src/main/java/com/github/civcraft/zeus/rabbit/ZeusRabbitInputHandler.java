package com.github.civcraft.zeus.rabbit;

import org.apache.logging.log4j.Logger;

import com.github.civcraft.zeus.model.TransactionIdManager;
import com.github.civcraft.zeus.rabbit.abstr.AbstractRabbitInputHandler;
import com.github.civcraft.zeus.rabbit.incoming.apollo.PlayerLoginRequest;
import com.github.civcraft.zeus.rabbit.incoming.artemis.AcceptPlayerJoin;
import com.github.civcraft.zeus.rabbit.incoming.artemis.ArtemisStartupHandler;
import com.github.civcraft.zeus.rabbit.incoming.artemis.PlayerDataRequestHandler;
import com.github.civcraft.zeus.rabbit.incoming.artemis.PlayerDataTargetConfirm;
import com.github.civcraft.zeus.rabbit.incoming.artemis.PlayerInitTransferRequest;
import com.github.civcraft.zeus.rabbit.incoming.artemis.PlayerLocationRequest;
import com.github.civcraft.zeus.rabbit.incoming.artemis.ReceivePlayerData;
import com.github.civcraft.zeus.rabbit.incoming.artemis.SendPlayerReply;
import com.github.civcraft.zeus.rabbit.incoming.artemis.ServerWhitelistLevelChange;

public class ZeusRabbitInputHandler extends AbstractRabbitInputHandler {

	private Logger logger;

	public ZeusRabbitInputHandler(TransactionIdManager transactionIdManager, Logger logger) {
		super(transactionIdManager);
		this.logger = logger;
	}

	@Override
	protected void registerCommands() {
		// apollo
		registerCommand(new PlayerLoginRequest());

		// artemis
		registerCommand(new AcceptPlayerJoin());
		registerCommand(new ArtemisStartupHandler());
		registerCommand(new PlayerDataRequestHandler());
		registerCommand(new PlayerDataTargetConfirm());
		registerCommand(new PlayerInitTransferRequest());
		registerCommand(new PlayerLocationRequest());
		registerCommand(new ReceivePlayerData());
		registerCommand(new SendPlayerReply());
		registerCommand(new ServerWhitelistLevelChange());

		registerCommand(new StandardRequestHandler());
		// registerCommand(new AddBroadcastInterest());
		// registerCommand(new RemoveBroadcastInterest());

	}

	@Override
	protected void logError(String msg) {
		logger.error(msg);
	}

}
