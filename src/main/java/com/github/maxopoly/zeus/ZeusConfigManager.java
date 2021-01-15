package com.github.maxopoly.zeus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.maxopoly.zeus.database.DBConnection;
import com.github.maxopoly.zeus.servers.ApolloServer;
import com.github.maxopoly.zeus.servers.ArtemisServer;
import com.github.maxopoly.zeus.servers.ConnectedServer;
import com.rabbitmq.client.ConnectionFactory;

public class ZeusConfigManager {

	private static final String configFileName = "config.json";
	private JSONObject config;
	private Logger logger;
	private boolean debugRabbit;
	private int whiteListLevel;
	private boolean allowNameChanges;
	private String lobbyShard;

	public ZeusConfigManager(Logger logger) {
		this.logger = logger;
	}

	public boolean reload() {
		final StringBuilder sb = new StringBuilder();
		try {
			Files.readAllLines(new File(configFileName).toPath()).forEach(sb::append);
			config = new JSONObject(sb.toString());
			parse();
			return true;
		} catch (IOException | JSONException e) {
			logger.error("Failed to load config file", e);
			return false;
		}
	}

	private void parse() {
		debugRabbit = config.getJSONObject("rabbitmq").optBoolean("debug", true);
		whiteListLevel = config.optInt("whitelist_level", 0);
		allowNameChanges = config.optBoolean("allow_namechanges", false);
		lobbyShard = config.optString("lobby", null);
	}

	public DBConnection getDatabase() {
		try {
			JSONObject json = config.getJSONObject("database");
			String user = json.optString("user", "root");
			String password = json.optString("password", null);
			String host = json.optString("host", "localhost");
			int port = json.getInt("port");
			String database = json.getString("database");
			int poolSize = json.optInt("pool_size", 5);
			return new DBConnection(logger, user, password, host, port, database, poolSize, 10000, 600000, 1800000);
		} catch (JSONException e) {
			logger.error("Failed to parse db credentials", e);
			return null;
		}
	}

	public ConnectionFactory getRabbitConfig() {
		try {
			JSONObject json = config.getJSONObject("rabbitmq");
			ConnectionFactory connFac = new ConnectionFactory();
			String user = json.optString("user", null);
			if (user != null) {
				connFac.setUsername(user);
			}
			String password = json.optString("password", null);
			if (password != null) {
				connFac.setPassword(password);
			}
			String host = json.optString("host", null);
			if (host != null) {
				connFac.setHost(host);
			}
			int port = json.optInt("port", -1);
			if (port != -1) {
				connFac.setPort(port);
			}
			return connFac;
		} catch (JSONException e) {
			logger.error("Failed to parse rabbit credentials", e);
			return null;
		}
	}

	public boolean debugRabbit() {
		return debugRabbit;
	}

	public int getWhiteListLevel() {
		return whiteListLevel;
	}
	
	public boolean allowNameChanges() {
		return allowNameChanges;
	}
	
	public String getLobbyShard() {
		return lobbyShard;
	}

	public List<ConnectedServer> parseClientServers() {
		List<ConnectedServer> result = new ArrayList<>();
		JSONArray serverJson = config.getJSONArray("servers");
		for (int i = 0; i < serverJson.length(); i++) {
			JSONObject json = serverJson.getJSONObject(i);
			String type = json.getString("type");
			ConnectedServer parsedServer;
			String id = json.getString("id");
			switch (type.toLowerCase()) {
			case "artemis":
			case "minecraft":
				parsedServer = new ArtemisServer(id);
				break;
			case "apollo":
			case "bungee":
				parsedServer = new ApolloServer(id);
				break;
			default:
				parsedServer = null;
			}
			if (parsedServer == null) {
				logger.error("Failed to parse server " + id + " from config");
				continue;
			}
			result.add(parsedServer);
		}
		return result;
	}

}
