package com.cs210.project.infrastructure.persistence;

import com.cs210.project.domain.account.Account;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;

public final class AppDatabase {

    private static final String DEFAULT_URL = "jdbc:mysql://mysql-8.0/car_rental_system";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static Database database;

    private AppDatabase() {
    }

    public static synchronized Database getDatabase() {
        if (database == null) {
            database = createDatabase(false);
        }
        return database;
    }

    public static synchronized void runMigrations() {
        Database migrationDatabase = createDatabase(true);
        migrationDatabase.shutdown();
    }

    public static synchronized void shutdown() {
        if (database != null) {
            database.shutdown();
            database = null;
        }
    }

    private static Database createDatabase(boolean runMigrations) {
        DatabaseConfig config = new DatabaseConfig();
        config.setName(runMigrations ? "migration" : "app");
        config.setRegister(false);
        config.setDefaultServer(false);
        config.setDdlGenerate(false);
        config.setDdlRun(false);
        config.setRunMigration(runMigrations);
        config.setDatabasePlatformName("mysql");
        config.addClass(Account.class);

        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriver("com.mysql.cj.jdbc.Driver");
        dataSourceConfig.setUrl(readSetting("db.url", "CAR_RENTAL_DB_URL", DEFAULT_URL));
        dataSourceConfig.setUsername(readSetting("db.user", "CAR_RENTAL_DB_USER", DEFAULT_USER));
        dataSourceConfig.setPassword(readSetting("db.password", "CAR_RENTAL_DB_PASSWORD", DEFAULT_PASSWORD));
        dataSourceConfig.setMinConnections(1);
        dataSourceConfig.setMaxConnections(8);

        config.setDataSourceConfig(dataSourceConfig);

        return DatabaseFactory.create(config);
    }

    private static String readSetting(String propertyName, String environmentName, String fallback) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        return fallback;
    }
}
