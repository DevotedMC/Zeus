package com.github.maxopoly.zeus.rabbit;

import org.apache.logging.log4j.Logger;

import com.github.maxopoly.zeus.model.TransactionIdManager;
import com.github.maxopoly.zeus.rabbit.abstr.AbstractRabbitInputHandler;
import com.github.maxopoly.zeus.rabbit.incoming.apollo.NotifyPlayerSwitchShardHandler;
import com.github.maxopoly.zeus.rabbit.incoming.apollo.PlayerDisconnectHandler;
import com.github.maxopoly.zeus.rabbit.incoming.apollo.PlayerLoginRequest;
import com.github.maxopoly.zeus.rabbit.incoming.apollo.WorldSpawnRequestHandler;
import com.github.maxopoly.zeus.rabbit.incoming.artemis.AcceptPlayerJoin;
import com.github.maxopoly.zeus.rabbit.incoming.artemis.ArtemisShutdownHandler;
import com.github.maxopoly.zeus.rabbit.incoming.artemis.ArtemisStartupHandler;
import com.github.maxopoly.zeus.rabbit.incoming.artemis.PlayerDataRequestHandler;
import com.github.maxopoly.zeus.rabbit.incoming.artemis.PlayerDataTargetConfirm;
import com.github.maxopoly.zeus.rabbit.incoming.artemis.PlayerInitTransferRequest;
import com.github.maxopoly.zeus.rabbit.incoming.artemis.PlayerLocationRequest;
import com.github.maxopoly.zeus.rabbit.incoming.artemis.ReceivePlayerData;
import com.github.maxopoly.zeus.rabbit.incoming.artemis.RequestPlayerNameHandler;
import com.github.maxopoly.zeus.rabbit.incoming.artemis.RequestPlayerUUIDHandler;
import com.github.maxopoly.zeus.rabbit.incoming.artemis.SendPlayerReply;
import com.github.maxopoly.zeus.rabbit.incoming.artemis.ServerWhitelistLevelChange;

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
		registerCommand(new PlayerDisconnectHandler());
		registerCommand(new WorldSpawnRequestHandler());
		registerCommand(new NotifyPlayerSwitchShardHandler());

		// artemis
		registerCommand(new AcceptPlayerJoin());
		registerCommand(new ArtemisStartupHandler());
		registerCommand(new ArtemisShutdownHandler());
		registerCommand(new PlayerDataRequestHandler());
		registerCommand(new PlayerDataTargetConfirm());
		registerCommand(new PlayerInitTransferRequest());
		registerCommand(new PlayerLocationRequest());
		registerCommand(new ReceivePlayerData());
		registerCommand(new SendPlayerReply());
		registerCommand(new ServerWhitelistLevelChange());
		registerCommand(new RequestPlayerNameHandler());
		registerCommand(new RequestPlayerUUIDHandler());
		

		// registerCommand(new AddBroadcastInterest());
		// registerCommand(new RemoveBroadcastInterest());

	}

	@Override
	protected void logError(String msg) {
		logger.error(msg);
	}

}
