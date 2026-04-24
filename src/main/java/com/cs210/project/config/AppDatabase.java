package com.cs210.project.config;

import com.cs210.project.models.Account;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;

public final class AppDatabase {

    private static final String DB_URL =
            "jdbc:mysql://mysql-8.0/car_rental_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static Database database;

    private AppDatabase() {
    }

    public static synchronized Database getDatabase() {
        if (database == null) {
            database = createDatabase();
        }
        return database;
    }

    public static synchronized void shutdown() {
        if (database != null) {
            database.shutdown();
            database = null;
        }
    }

    private static Database createDatabase() {
        DatabaseConfig config = new DatabaseConfig();
        config.setName("app");
        config.setRegister(false);
        config.setDefaultServer(false);
        config.setDdlGenerate(false);
        config.setDdlRun(false);
        config.setRunMigration(false);
        config.setDatabasePlatformName("mysql");
        config.addClass(Account.class);

        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriver("com.mysql.cj.jdbc.Driver");
        dataSourceConfig.setUrl(DB_URL);
        dataSourceConfig.setUsername(DB_USER);
        dataSourceConfig.setPassword(DB_PASSWORD);
        dataSourceConfig.setMinConnections(1);
        dataSourceConfig.setMaxConnections(8);

        config.setDataSourceConfig(dataSourceConfig);

        return DatabaseFactory.create(config);
    }
}
