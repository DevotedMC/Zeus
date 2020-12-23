package com.github.civcraft.zeus.model;

import java.util.UUID;

public class PlayerNBT {

	private byte[] rawCompound;
	private ZeusLocation location;
	private int version;
	private UUID player;

	public int getVersion() {
		return version;
	}

	public UUID getPlayer() {
		return player;
	}

	public byte[] getRawData() {
		return rawCompound;
	}

	public ZeusLocation getLocation() {
		return location;
	}

}
