package com.github.civcraft.zeus.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.logging.log4j.Logger;

public class DAO {

	private static final String timestampField = "last_updated timestamp with time zone not null default now()";

	private DBConnection db;
	private Logger logger;

	public DAO(DBConnection connection, Logger logger) {
		this.logger = logger;
		this.db = connection;
		if (!createTables()) {
			logger.error("Failed to init DB, shutting down");
			System.exit(1);
		}
	}

	private boolean createTables() {
		try (Connection conn = db.getConnection()) {
			try (PreparedStatement prep = conn.prepareStatement("CREATE TABLE IF NOT EXISTS players "
					+ "(player uuid primary key, data blob not null, version int not null, "
					+ "world varchar(255) not null, loc_x double not null, loc_y double not null, "
					+ "loc_z double not null );")) {
				prep.execute();
			}

		} catch (SQLException e) {
			logger.error("Failed to create table", e);
			return false;
		}
		return true;
	}

}
