package com.github.civcraft.zeus.rabbit;

import org.apache.logging.log4j.Logger;

import com.github.civcraft.zeus.model.TransactionIdManager;
import com.github.civcraft.zeus.rabbit.abstr.AbstractRabbitInputHandler;

public class ZeusRabbitInputHandler extends AbstractRabbitInputHandler {
	
	private Logger logger;
	
	public ZeusRabbitInputHandler(TransactionIdManager transactionIdManager, Logger logger) {
		super(transactionIdManager);
		this.logger = logger;
	}

	@Override
	protected void registerCommands() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void logError(String msg) {
		logger.error(msg);
	}

}
