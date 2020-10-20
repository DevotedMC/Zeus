package com.github.civcraft.zeus.rabbit.outgoing.artemis;

import com.github.civcraft.zeus.rabbit.RabbitMessage;

public class GlobalPlayerLogout extends RabbitMessage {

	public GlobalPlayerLogout(String transactionID) {
		super(transactionID);
		// TODO Auto-generated constructor stub
	}

}
