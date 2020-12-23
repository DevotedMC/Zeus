package com.github.civcraft.zeus.database;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.logging.log4j.Logger;

import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class ZeusDAO {

	private DBConnection db;
	private Logger logger;

	public ZeusDAO(DBConnection connection, Logger logger) {
		this.logger = logger;
		this.db = connection;
	}

	public boolean createTables() {
		try (Connection conn = db.getConnection()) {
			try (PreparedStatement prep = conn.prepareStatement("CREATE TABLE IF NOT EXISTS players "
					+ "(player uuid primary key, data bytea, active_server varchar(255), "
					+ "world varchar(255), loc_x double precision, loc_y double precision, loc_z double precision);")) {
				prep.execute();
			}
			try (PreparedStatement prep = conn.prepareStatement("CREATE OR REPLACE FUNCTION insert_player_data "
					+ "(in_lock bigint, in_player uuid, in_data bytea, in_server varchar(255), in_world varchar(255),"
					+ "in_loc_x double precision, in_loc_y double precision, in_loc_z double precision) "
					+ "returns int " + "language plpgsql as $$ " + "declare" + "  existing_data players%rowtype;"
					+ " begin " + "perform pg_advisory_lock(in_lock); " + "select * from players into existing_data ;"
					+ "if not found then" + "  perform pg_advisory_unlock(in_lock);" + "  return 1;" // no prepared
																										// entry
																										// available to
																										// write data to
					+ "else" + "  if existing_data.active_server != in_server then"
					+ "    perform pg_advisory_unlock(in_lock);" + "    return 2;" // existing data lock from other
																					// server
					+ "  else" + "    update players set data = in_data, active_server = null, world = in_world, "
					+ "      loc_x = in_loc_x, loc_y = in_loc_y, loc_z = in_loc_z where player = in_player;"
					+ "  end if; " + "end if; " + "perform pg_advisory_unlock(in_lock); " + "return 3;" // success
					+ "end;" + "$$")) {
				prep.execute();
			}
			try (PreparedStatement prep = conn.prepareStatement("CREATE OR REPLACE FUNCTION prep_player_login "
					+ "(in_lock bigint, in_player uuid, in_server varchar(255))" + " returns bytea "
					+ "language plpgsql " + "as $$ " + "declare" + "  existing_data players%rowtype;" + " begin "
					+ "perform pg_advisory_lock(in_lock);"
					+ "select * into existing_data from players where player = in_player;" + "if not found then" // initial
																													// data
																													// insert
					+ "  insert into players (player, data, active_server, world, loc_x, loc_y, loc_z) "
					+ "    values(in_player, null, in_server, null, null, null, null);"
					+ "  perform pg_advisory_unlock(in_lock);" + "  return E'\\\\000'::bytea;" // target server will
																								// generate initial data
					+ "else " + "  if existing_data.active_server is not null then"
					+ "    perform pg_advisory_unlock(in_lock);" + "    return  E'\\\\001'::bytea;" // signals error
					+ "  else" + "    update players set active_server = in_server where player = in_player;"
					+ "    perform pg_advisory_unlock(in_lock);" + "    return existing_data.data;" + "  end if;"
					+ "end if;" + "end;" + "$$")) {
				prep.execute();
			}
			try (PreparedStatement prep = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS whitelist " + "(player uuid primary key, level INT(11) NOT NULL);")) {
				prep.execute();
			}

		} catch (SQLException e) {
			logger.error("Failed to setup tables and procedures", e);
			return false;
		}
		return true;
	}

	public int getWhitelistLevel(UUID uuid) {
		try (Connection conn = db.getConnection();
				PreparedStatement prep = conn.prepareStatement("select level from whitelist where player = ?")) {
			prep.setObject(1, uuid);
			try (ResultSet rs = prep.executeQuery()) {
				if (!rs.next()) {
					return 0;
				}
				return rs.getInt(1);
			}

		} catch (SQLException e) {
			logger.error("Failed to load player whitelist level", e);
			return Integer.MAX_VALUE;
		}
	}

	public void setWhitelistLevel(UUID uuid, int level) {
		if (level < 0) {
			throw new IllegalArgumentException();
		}
		if (level == 0) {
			try (Connection conn = db.getConnection();
					PreparedStatement prep = conn.prepareStatement("delete from whitelist where player = ?")) {
				prep.setObject(1, uuid);
				prep.execute();
			} catch (SQLException e) {
				logger.error("Failed to delete whitelist entry", e);
			}
			return;
		}
		// TODO insert and update on key collision
	}

	public byte[] loadAndLockPlayerNBT(UUID player, ConnectedServer server) {
		logger.info(String.format("Loading data for %s from %s", player, server.getID()));
		try (Connection conn = db.getConnection();
				PreparedStatement prep = conn.prepareStatement("select * from prep_player_login (?,?,?)")) {
			prep.setLong(1, player.getMostSignificantBits());
			prep.setObject(2, player);
			prep.setString(3, server.getID());
			try (ResultSet rs = prep.executeQuery()) {
				rs.next();
				byte[] data = rs.getBytes(1);
				if (data != null && data.length == 1) {
					if (data[0] == 0) {
						data = new byte[0]; // signals target MC server to create data
					} else {
						data = null; // error
					}
				}
				return data;
			}
		} catch (SQLException e) {
			logger.error("Failed to load and lock player data", e);
			return null;
		}
	}

	public ZeusLocation getLocation(UUID uuid) {
		try (Connection conn = db.getConnection();
				PreparedStatement prep = conn
						.prepareStatement("select world, loc_x, loc_y, loc_z from players where player = ?;")) {
			prep.setObject(1, uuid);
			try (ResultSet rs = prep.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				String world = rs.getString(1);
				if (world == null) {
					return null;
				}
				double x = rs.getDouble(2);
				double y = rs.getDouble(3);
				double z = rs.getDouble(4);
				return new ZeusLocation(world, x, y, z);
			}

		} catch (SQLException e) {
			logger.error("Failed to load player location", e);
			return null;
		}
	}

	public boolean savePlayerNBT(UUID player, byte[] data, ZeusLocation location, ConnectedServer server) {
		logger.info("Saving data for " + player + " from " + server.getID());
		try (Connection conn = db.getConnection();
				PreparedStatement prep = conn.prepareStatement("select * from insert_player_data (?,?,?,?,?,?,?,?)")) {
			prep.setLong(1, player.getMostSignificantBits());
			prep.setObject(2, player);
			prep.setBinaryStream(3, new ByteArrayInputStream(data), data.length);
			prep.setString(4, server.getID());
			prep.setString(5, location.getWorld());
			prep.setDouble(6, location.getX());
			prep.setDouble(7, location.getY());
			prep.setDouble(8, location.getZ());
			try (ResultSet rs = prep.executeQuery()) {
				rs.next();
				int status = rs.getInt(1);
				switch (status) {
				case 1:
					logger.error(String.format("Failed to save data for %s, no prepared entry in the db could be found",
							player));
					return false;
				case 2:
					logger.error(String.format(
							"Failed to insert data for %s, another server is holding the player data lock", player));
					return false;
				case 3:
					return true;
				default:
					throw new IllegalStateException("Illegal return code when saving player data");
				}
			}
		} catch (SQLException e) {
			logger.error("Failed to save player NBT", e);
			return false;
		}
	}

}
