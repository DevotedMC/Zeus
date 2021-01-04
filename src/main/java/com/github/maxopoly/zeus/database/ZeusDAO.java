package com.github.maxopoly.zeus.database;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.logging.log4j.Logger;

import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.plugin.ZeusPluginDatabase;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class ZeusDAO extends ZeusPluginDatabase {

	public ZeusDAO(Logger logger) {
		super("zeus", logger);
		registerMigrations();
	}

	private void registerMigrations() {
		registerMigration(1, "CREATE TABLE IF NOT EXISTS player_data "
				+ "(player uuid primary key, data bytea, active_server varchar(255), "
				+ "world varchar(255), loc_x double precision, loc_y double precision, loc_z double precision);",

				"CREATE TABLE IF NOT EXISTS player_names (player uuid primary key, name varchar(16));",

				"CREATE UNIQUE INDEX player_names_lower ON player_names ((lower(name)));",

				// this was a lot better before it got somehow autoformatted. Let's wait for
				// proper java 15 support (text blocks) before we fix it
				"CREATE OR REPLACE FUNCTION insert_player_data "
						+ "(in_lock bigint, in_player uuid, in_data bytea, in_server varchar(255), in_world varchar(255),"
						+ "in_loc_x double precision, in_loc_y double precision, in_loc_z double precision) "
						+ "returns int " + "language plpgsql as $$ " + "declare" + "  existing_data player_data%rowtype;"
						+ " begin " + "perform pg_advisory_lock(in_lock); "
						+ "select * from player_data into existing_data where player = in_player;" + "if not found then"
						+ "  perform pg_advisory_unlock(in_lock);" + "  return 1;" // no prepared
																					// entry
																					// available to
																					// write data to
						+ "else" + "  if existing_data.active_server != in_server then"
						+ "    perform pg_advisory_unlock(in_lock);" + "    return 2;" // existing data lock from other
																						// server
						+ "  else" + "    update player_data set data = in_data, active_server = null, world = in_world, "
						+ "      loc_x = in_loc_x, loc_y = in_loc_y, loc_z = in_loc_z where player = in_player;"
						+ "  end if; " + "end if; " + "perform pg_advisory_unlock(in_lock); " + "return 3;" // success
						+ "end;" + "$$",

				"CREATE OR REPLACE FUNCTION prep_player_login "
						+ "(in_lock bigint, in_player uuid, in_server varchar(255))" + " returns bytea "
						+ "language plpgsql " + "as $$ " + "declare" + "  existing_data player_data%rowtype;" + " begin "
						+ "perform pg_advisory_lock(in_lock);"
						+ "select * into existing_data from player_data where player = in_player;" + "if not found then" // initial
																														// data
																														// insert
						+ "  insert into player_data (player, data, active_server, world, loc_x, loc_y, loc_z) "
						+ "    values(in_player, null, in_server, null, null, null, null);"
						+ "  perform pg_advisory_unlock(in_lock);" + "  return E'\\\\000'::bytea;" // target server will
																									// generate initial
																									// data
						+ "else " + "  if existing_data.active_server is not null then"
						+ "    perform pg_advisory_unlock(in_lock);" + "    return  E'\\\\001'::bytea;" // signals error
						+ "  else" + "    update player_data set active_server = in_server where player = in_player;"
						+ "    perform pg_advisory_unlock(in_lock);" + "    return existing_data.data;" + "  end if;"
						+ "end if;" + "end;" + "$$",

				"CREATE TABLE IF NOT EXISTS whitelist (player uuid primary key, level INT NOT NULL);");
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

	public String getPlayerName(UUID uuid) {
		try (Connection conn = db.getConnection();
				PreparedStatement prep = conn
						.prepareStatement("select name from player_names where player = ?")) {
			prep.setObject(1, uuid);
			try (ResultSet rs = prep.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				return rs.getString(1);
			}

		} catch (SQLException e) {
			logger.error("Failed to get player name", e);
			return null;
		}
	}
	
	public UUID getPlayerUUID(String name) {
		name = name.toLowerCase();
		try (Connection conn = db.getConnection();
				PreparedStatement prep = conn
						.prepareStatement("select player from player_names where lower(name) = ?")) {
			prep.setString(1, name);
			try (ResultSet rs = prep.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				return (UUID) rs.getObject(1);
			}

		} catch (SQLException e) {
			logger.error("Failed to get player uuid", e);
			return null;
		}
	}

	public void setPlayerName(UUID uuid, String name) {
		try (Connection conn = db.getConnection();
				PreparedStatement prep = conn.prepareStatement(
						"insert into player_names(player,name) values(?,?) on conflict (player) do update set name = EXCLUDED.name")) {
			prep.setObject(1, uuid);
			prep.setString(2, name);
			prep.execute();
		} catch (SQLException e) {
			logger.error("Failed to set player name", e);
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
		try (Connection conn = db.getConnection();
				PreparedStatement prep = conn.prepareStatement(
						"insert into whitelist(player,level) values(?,?) on conflict (player) do update set level = EXCLUDED.level")) {
			prep.setObject(1, uuid);
			prep.setInt(2, level);
			prep.execute();
		} catch (SQLException e) {
			logger.error("Failed to set whitelist level", e);
		}
	}
	
	public String getServerLockFor(UUID uuid) {
		try (Connection conn = db.getConnection();
				PreparedStatement prep = conn
						.prepareStatement("select active_server from player_data where player = ?")) {
			prep.setObject(1, uuid);
			try (ResultSet rs = prep.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				return rs.getString(1);
			}

		} catch (SQLException e) {
			logger.error("Failed to get active player server", e);
			return null;
		}
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
						.prepareStatement("select world, loc_x, loc_y, loc_z from player_data where player = ?;")) {
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
		logger.info(String.format("Saving data for %s from %s", player, server.getID()));
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
