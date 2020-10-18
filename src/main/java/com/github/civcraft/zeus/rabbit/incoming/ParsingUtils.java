package com.github.civcraft.zeus.rabbit.incoming;

import org.json.JSONObject;

import com.github.civcraft.zeus.model.Location;

public class ParsingUtils {

	public static Location parseLocation(JSONObject json) {
		double x = json.getDouble("x");
		double y = json.getDouble("y");
		double z = json.getDouble("z");
		return new Location(x, y, z);
	}
}
