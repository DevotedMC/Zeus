package com.github.civcraft.zeus.database;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import org.apache.logging.log4j.Logger;

import com.github.civcraft.zeus.model.PlayerNBT;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class DAO {
	
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
					+ "(player uuid primary key, data blob, active_server varchar(255), "
					+ "world varchar(255), loc_x double, loc_y double, loc_z double);")) {
				prep.execute();
			}
			try (PreparedStatement prep = conn.prepareStatement("CREATE OR REPLACE PROCEDURE insert_player_data "
					+ "(in_lock big_int, in_player uuid, in_data blob, in_server varchar(255), in_world varchar(255),"
					+ "in_loc_x double, in_loc_y double, in_loc_z double) "
					+ "language plpgsql as $$ "
					+ "declare"
					+ "  existing_data players%rowtype;"
					+ "begin"
					+ "PERFORM pg_advisory_lock(in_lock);"
					+ "select data, active_server, world, loc_x, loc_y, loc_z from players into existing_data ;"
					+ "if not found then"
					+ "  PERFORM pg_advisory_unlock(in_lock);"
					+ "  RAISE EXCEPTION 'No prepared entry for %', in_player;"
					+ "else"
					+ "  if existing_data.active_server != in_server then"
					+ "    PERFORM pg_advisory_unlock(in_lock);"
					+ "    RAISE EXCEPTION 'Bad data source for %', in_player;"
					+ "  else"
					+ "    update players set data = in_date, active_server = null, world = in_world, "
					+ "      loc_x = in_loc_x, loc_y = in_loc_y, loc_z = in_loc_z where player = in_player;"
					+ "  endif;"
					+ "endif;"
					+ "commit;"
					+ "PERFORM pg_advisory_unlock(in_lock);"
					+ "end;$$")) {
				prep.execute();
			}
			try (PreparedStatement prep = conn.prepareStatement("CREATE OR REPLACE PROCEDURE prep_player_login "
					+ "(in_lock big_int, in_player uuid, in_server varchar(255), out out_data blob)"
					+ "language plpgsql as $$ "
					+ "declare"
					+ "  existing_data players%rowtype;"
					+ "begin"
					+ "PERFORM pg_advisory_lock(in_lock);"
					+ "select data, active_server, world, loc_x, loc_y, loc_z from players into existing_data ;"
					+ "if not found then" //initial data insert
					+ "  insert into players (player, data, active_server, world, loc_x, loc_y, loc_z) "
					+ "    values(in_player, null, in_server, null, null, null, null);"
					+ "  out_data = null;" //target server will generate initial data
					+ "else"
					+ "  if existing_data.active_server != null then"
					+     "PERFORM pg_advisory_unlock(in_lock);" //always release lock
					+ "    RAISE EXCEPTION 'Preexisting active session %', existing_data.active_server;"
					+ "  else"
					+ "    update players set active_server = in_server where player = in_uuid;"
					+ "    out_data = existing_data.data;"
					+ "  endif;"
					+ "endif;"
					+ "commit;"
					+ "PERFORM pg_advisory_unlock(in_lock);"
					+ "end;$$")) {
				prep.execute();
			}

		} catch (SQLException e) {
			logger.error("Failed to setup tables and procedures", e);
			return false;
		}
		return true;
	}

	public byte [] loadAndLockPlayerNBT(UUID player, ConnectedServer server) {
		try (Connection conn = db.getConnection();
				CallableStatement prep = conn.prepareCall("call prep_player_login (?,?,?);")) {
			prep.setLong(1, player.getMostSignificantBits());
			prep.setObject(2, player);
			prep.setString(3, server.getID());
			prep.registerOutParameter(1, Types.BLOB);
			prep.execute();
			Blob blob = prep.getBlob(1);
			return blob.getBytes(1L, (int) blob.length());
		} catch (SQLException e) {
			logger.error("Failed to load and lock player data", e);
			return null;
		}
	}

	public boolean savePlayerNBT(PlayerNBT data, ConnectedServer server) {
		try (Connection conn = db.getConnection();
				CallableStatement prep = conn.prepareCall("call insert_player_data (?,?,?,?,?,?,?,?);")) {
			prep.setLong(1, data.getPlayer().getMostSignificantBits());
			prep.setObject(2, data.getPlayer());
			prep.setBinaryStream(3, new ByteArrayInputStream(data.getRawData()), 
					data.getRawData().length);
			prep.setString(4, server.getID());
			prep.setString(5, data.getLocation().getWorld());
			prep.setDouble(6, data.getLocation().getX());
			prep.setDouble(7, data.getLocation().getY());
			prep.setDouble(8, data.getLocation().getZ());
			prep.execute();
			return true;
		} catch (SQLException e) {
			logger.error("Failed to save player NBT", e);
			return false;
		}
	}

}
