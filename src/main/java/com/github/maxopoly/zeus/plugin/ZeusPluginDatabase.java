package com.github.maxopoly.zeus.plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.Logger;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.database.DBConnection;

/**
 * Very heavily inspired by CivModCores ManagedDatasource, thank you
 * ProgrammerDan
 */
public class ZeusPluginDatabase {

	protected final DBConnection db;
	protected final Logger logger;
	private final ExecutorService postExecutor;
	private final TreeMap<Integer, Migration> migrations;
	private int firstMigration;
	private int lastMigration;
	protected final String identifier;

	public ZeusPluginDatabase(String identifier, Logger logger) {
		this.logger = logger;
		this.identifier = identifier;
		this.db = ZeusMain.getInstance().getConfigManager().getDatabase();
		this.postExecutor = Executors.newSingleThreadExecutor();
		this.migrations = new TreeMap<>();
		this.firstMigration = Integer.MAX_VALUE;
		this.lastMigration = Integer.MIN_VALUE;
	}

	/**
	 * Use this to register a migration. After all migrations have been registered,
	 * call {@link #updateDatabase()}.
	 *
	 * This is <i>not</i> checked for completeness or accuracy.
	 *
	 * @param id      The migration ID -- 0, 1, 2 etc, must be unique.
	 * @param queries The queries to run, in sequence.
	 */
	protected void registerMigration(int id, String... queries) {
		registerMigration(id, null, queries);
	}

	/**
	 * Use this to register a migration. After all migrations have been registered,
	 * call {@link #updateDatabase()}.
	 * 
	 * This is <i>not</i> checked for completeness or accuracy.
	 * 
	 * @param id           The migration ID -- 0, 1, 2 etc, must be unique.
	 * @param ignoreErrors Indicates if errors in this migration should be ignored.
	 * @param callback     An optional callback that'll run after the migration has
	 *                     completed.
	 * @param queries      The queries to run, in sequence.
	 */
	protected void registerMigration(int id, Callable<Boolean> callback, String... queries) {
		this.migrations.put(id, new Migration(callback, queries));
		if (id > lastMigration) {
			lastMigration = id;
		}
		if (id < firstMigration) {
			firstMigration = id;
		}
	}

	/**
	 * This method should be called by your plugin after all migrations have been
	 * registered.
	 *
	 * 1. Check for current update level. a. If no record exists, start with first
	 * migration, and apply in sequence from first to last, updating the migration
	 * management table along the way b. If a record exists, read which migration
	 * was completed last i. If identical to "highest" registered migration level,
	 * do nothing. ii. If less then "highest" registered migration level, get the
	 * tailset of migrations "after" the last completed level, and run. 4. If no
	 * errors occurred, or this migration has errors marked ignored, return true. 5.
	 * If errors, return false.
	 * 
	 * @return As described in the algorithm above, returns true if no errors or all
	 *         ignored; or false if errors occurred.
	 */
	public boolean updateDatabase() {
		setupMigrationTables();
		// Check update level, etc.
		int currentLevel;
		if (migrations.isEmpty()) {
			currentLevel = -1;
		} else {
			currentLevel = migrations.firstKey() - 1;
		}
		try (Connection connection = db.getConnection();
				PreparedStatement statement = connection.prepareStatement(
						"SELECT current_migration_number FROM ze_plugin_versions WHERE plugin_name = ?")) {
			statement.setString(1, identifier);
			try (ResultSet set = statement.executeQuery();) {
				if (set.next()) {
					currentLevel = set.getInt(1);
				} // else we aren't tracked yet!
			}
		} catch (SQLException e) {
			logger.error("Unable to check last migration!", e);
			return false;
		}
		NavigableMap<Integer, Migration> newApply = migrations.tailMap(currentLevel, false);
		try {
			if (newApply.size() > 0) {
				logger.info(String.format("%s database is behind, %s migrations found", identifier, newApply.size()));
				if (doMigrations(newApply)) {
					logger.info(identifier + " fully migrated.");
				} else {
					logger.warn(identifier + " failed to apply updates.");
					return false;
				}
			} else {
				logger.info(identifier + " database is up to date.");
			}
			return true;
		} catch (Exception exception) {
			logger.error(identifier + " failed to apply updates for some reason...");
			logger.error("Full exception: ", exception);
			return false;
		}
	}

	private boolean doMigrations(NavigableMap<Integer, Migration> migrations) {
		try {
			for (Integer id : migrations.keySet()) {
				logger.info("Migration [" + id + " ] Applying");
				Migration migration = migrations.get(id);
				if (migration == null) {
					continue; // huh?
				}
				if (doMigration(id, migration.migrations, migration.postMigration)) {
					logger.info("Migration [" + id + " ] Successful");
					try (Connection connection = db.getConnection();
							PreparedStatement statement = connection.prepareStatement("INSERT INTO ze_plugin_versions "
									+ "(plugin_name, current_migration_number, last_migration) "
									+ "VALUES (?, ?, NOW()) on conflict (plugin_name) do update "
									+ "set current_migration_number = EXCLUDED.current_migration_number, "
									+ "last_migration = EXCLUDED.last_migration")) {
						statement.setString(1, identifier);
						statement.setInt(2, id);
						if (statement.executeUpdate() < 1) {
							logger.warn("Might not have recorded migration " + id + " occurrence successfully.");
						}
					} catch (SQLException exception) {
						logger.error("Failed to record migration " + id + " occurrence successfully.", exception);
						return false;
					}
				} else {
					logger.error("Migration [" + id + " ] Failed");
					return false;
				}
			}
			return true;
		} catch (Exception exception) {
			logger.error("Unexpected failure during migrations", exception);
			return false;
		}
	}

	private boolean doMigration(Integer migration, List<String> queries, Callable<Boolean> post) {
		String lastQuery = null;
		try (Connection connection = db.getConnection();) {
			for (String query : queries) {
				lastQuery = query;
				try (Statement statement = connection.createStatement();) {
					statement.executeUpdate(query);

					SQLWarning warning = statement.getWarnings();
					while (warning != null) {
						logger.warn("Migration [" + migration + " ] Warning: " + warning.getMessage());
						warning = warning.getNextWarning();
					}
				}
			}
		} catch (SQLException exception) {
			logger.error("Migration [" + migration + " ] Failed migration: " + " For query: " + lastQuery, exception);
			return false;
		}
		if (post != null) {
			Future<Boolean> doing = postExecutor.submit(post);
			try {
				if (doing.get()) {
					logger.info("Migration " + migration + " ] Post Call Complete");
				} else {
					logger.error("Migration [" + migration + " ] Post Call failed!");
					return false;
				}
			} catch (Exception exception) {
				logger.error("Migration [" + migration + " ] Post Call failed!", exception);
				return false;
			}
		}
		return true;
	}

	private void setupMigrationTables() {
		try (Connection connection = db.getConnection();) {
			try (Statement statement = connection.createStatement();) {
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS ze_plugin_versions (managed_id SERIAL PRIMARY KEY, "
						+ "plugin_name VARCHAR(120) UNIQUE NOT NULL, management_began TIMESTAMP NOT NULL DEFAULT NOW(), "
						+ "current_migration_number INT NOT NULL, last_migration TIMESTAMP);");
			}
		} catch (SQLException e) {
			logger.error("Failed to prepare migrations table or register this plugin to it.", e);
		}
	}

	private static class Migration {
		public List<String> migrations;
		public Callable<Boolean> postMigration;

		public Migration(Callable<Boolean> postMigration, String... migrations) {
			this.migrations = Arrays.asList(migrations);
			this.postMigration = postMigration;
		}
	}

}
