package com.github.civcraft.zeus.database;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import org.apache.logging.log4j.Logger;

import com.github.civcraft.zeus.model.PlayerNBT;
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
					+ "returns void "
					+ "language plpgsql as $$ "
					+ "declare"
					+ "  existing_data players%rowtype;"
					+ " begin "
					+ "select pg_advisory_lock(in_lock); "
					+ "select data, active_server, world, loc_x, loc_y, loc_z from players into existing_data ;"
					+ "if not found then"
					+ "  select pg_advisory_unlock(in_lock);"
					+ "  RAISE EXCEPTION 'No prepared entry for %', in_player; "
					+ "else"
					+ "  if existing_data.active_server != in_server then"
					+ "    select pg_advisory_unlock(in_lock);"
					+ "    RAISE EXCEPTION 'Bad data source for %', in_player;"
					+ "  else"
					+ "    update players set data = in_date, active_server = null, world = in_world, "
					+ "      loc_x = in_loc_x, loc_y = in_loc_y, loc_z = in_loc_z where player = in_player;"
					+ "  end if; "
					+ "end if; "
					+ "select pg_advisory_unlock(in_lock); "
					+ "end;"
					+ "$$")) {
				prep.execute();
			}
			try (PreparedStatement prep = conn.prepareStatement("CREATE OR REPLACE FUNCTION prep_player_login "
					+ "(in_lock bigint, in_player uuid, in_server varchar(255))"
					+ "returns bytea "
					+ "language plpgsql as $$ "
					+ "declare"
					+ "  existing_data players%rowtype;"
					+ " begin "
					+ "select pg_advisory_lock(in_lock);"
					+ "select data, active_server, world, loc_x, loc_y, loc_z from players into existing_data ;"
					+ "if not found then" //initial data insert
					+ "  insert into players (player, data, active_server, world, loc_x, loc_y, loc_z) "
					+ "    values(in_player, null, in_server, null, null, null, null);"
					+ "  return null;" //target server will generate initial data
					+ "else "
					+ "  if existing_data.active_server != null then"
					+ "    select pg_advisory_unlock(in_lock);" //always release lock
					+ "    RAISE EXCEPTION 'Preexisting active session %', existing_data.active_server;"
					+ "  else"
					+ "    update players set active_server = in_server where player = in_uuid;"
					+ "    return existing_data.data;"
					+ "  end if;" 
					+ "end if;"
					+ "select pg_advisory_unlock(in_lock);"
					+ "end;"
					+ "$$")) {
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
	
	public ZeusLocation getLocation(UUID uuid) {
		try (Connection conn = db.getConnection();
				PreparedStatement 
				prep = conn.prepareStatement("select world, loc_x, loc_y, loc_z from player where uuid = ?;")) {
			prep.setObject(1, uuid);
			try (ResultSet rs = prep.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				String world = rs.getString(1);
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
