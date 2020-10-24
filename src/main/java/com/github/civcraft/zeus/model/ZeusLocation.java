package com.github.civcraft.zeus.model;

import org.json.JSONObject;

import com.google.common.base.Preconditions;

public class ZeusLocation {
	
	public static ZeusLocation parseLocation(JSONObject json) {
		String world = json.getString("world");
		double x = json.getDouble("x");
		double y = json.getDouble("y");
		double z = json.getDouble("z");
		return new ZeusLocation(world, x, y, z);
	}
	
	private final String world;
	private final double x;
	private final double y;
	private final double z;
	
	public ZeusLocation(String world, double x, double y, double z) {
		Preconditions.checkNotNull(world);
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String getWorld() {
		return world;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public void writeToJson(JSONObject json) {
		json.put("world", world);
		json.put("x", x);
		json.put("y", y);
		json.put("z", z);
	}

}
