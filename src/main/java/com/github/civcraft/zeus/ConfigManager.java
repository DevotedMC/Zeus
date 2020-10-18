package com.github.civcraft.zeus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class ConfigManager {
	
	private static final String configFileName = "config.json";
	private JSONObject config;
	private Logger logger;

	public ConfigManager(Logger logger) {
		this.logger = logger;
	}
	
	public boolean reload() {
		final StringBuilder sb = new StringBuilder();
		try {
			Files.readAllLines(new File(configFileName).toPath()).forEach(sb::append);
			config = new JSONObject(sb.toString());
			return true;
		} catch (IOException | JSONException e) {
			logger.error("Failed to load config file", e);
			return false;
		}
	}

}
