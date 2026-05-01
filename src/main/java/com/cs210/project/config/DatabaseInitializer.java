package com.cs210.project.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseInitializer {

    private static final String DATASET_RESOURCE = "/sql/car_rental_system_dataset.sql";
    private static final String FORCE_BOOTSTRAP_PROPERTY = "db.bootstrap.force";

    private DatabaseInitializer() {
    }

    public static void initializeFromConfig() {
        boolean forceBootstrap = Boolean.parseBoolean(System.getProperty(FORCE_BOOTSTRAP_PROPERTY, "false"));

        try {
            initialize(forceBootstrap);
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Failed to initialize database from bundled dataset.", e);
        }
    }

    public static void initialize(boolean forceBootstrap) throws SQLException, IOException {
        try (Connection serverConnection = DatabaseConnection.getServerConnection();
             Statement statement = serverConnection.createStatement()) {
            if (forceBootstrap) {
                statement.execute("DROP DATABASE IF EXISTS " + DatabaseConnection.DATABASE_NAME);
            }

            statement.execute(
                    "CREATE DATABASE IF NOT EXISTS " + DatabaseConnection.DATABASE_NAME
                            + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }

        try (Connection databaseConnection = DatabaseConnection.getConnection()) {
            if (forceBootstrap || shouldBootstrap(databaseConnection)) {
                runDataset(databaseConnection);
            }
            runMigrations(databaseConnection);
        }
    }

    private static void runMigrations(Connection connection) throws SQLException {
        addColumnIfMissing(connection, "vehicles", "image_path", "ALTER TABLE vehicles ADD COLUMN image_path VARCHAR(500) NULL");
        addColumnIfMissing(connection, "vehicles", "transmission_type", "ALTER TABLE vehicles ADD COLUMN transmission_type INT DEFAULT 1");
        addColumnIfMissing(connection, "vehicles", "fuel_type", "ALTER TABLE vehicles ADD COLUMN fuel_type INT DEFAULT 1");
        addColumnIfMissing(connection, "vehicles", "fuel_level", "ALTER TABLE vehicles ADD COLUMN fuel_level INT DEFAULT 100");
        addColumnIfMissing(connection, "persons", "birth_date", "ALTER TABLE persons ADD COLUMN birth_date DATE NULL");
        normalizeReservationDateColumns(connection);
        addColumnIfMissing(connection, "vehicle_reservations", "pickup_date",
                "ALTER TABLE vehicle_reservations ADD COLUMN pickup_date DATETIME NULL AFTER creation_date");
        ensureReturnInspectionsTable(connection);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("UPDATE vehicle_reservations SET pickup_date = creation_date WHERE pickup_date IS NULL");
            statement.executeUpdate("UPDATE vehicles SET transmission_type = 1 WHERE transmission_type IS NULL");
            statement.executeUpdate("UPDATE vehicles SET fuel_type = 1 WHERE fuel_type IS NULL");
            statement.executeUpdate("UPDATE vehicles SET fuel_level = 100 WHERE fuel_level IS NULL");
            statement.executeUpdate("UPDATE persons SET birth_date = '1995-01-01' WHERE birth_date IS NULL");
        }
    }

    private static void addColumnIfMissing(Connection connection, String tableName, String columnName, String sql) throws SQLException {
        if (!columnExists(connection, tableName, columnName)) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }
    }

    private static void normalizeReservationDateColumns(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE vehicle_reservations MODIFY due_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
            statement.execute("ALTER TABLE vehicle_reservations MODIFY creation_date DATETIME NOT NULL");
            statement.execute("ALTER TABLE vehicle_reservations MODIFY due_date DATETIME NOT NULL");
            statement.execute("ALTER TABLE vehicle_reservations MODIFY return_date DATETIME NULL");
        }
    }

    private static boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getColumns(connection.getCatalog(), null, tableName, columnName)) {
            return resultSet.next();
        }
    }

    private static void ensureReturnInspectionsTable(Connection connection) throws SQLException {
        if (tableExists(connection, "return_inspections")) {
            return;
        }

        String sql = """
                CREATE TABLE return_inspections (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    reservation_id INT NOT NULL,
                    vehicle_id INT NOT NULL,
                    worker_account_id INT NOT NULL,
                    inspection_date TIMESTAMP NOT NULL,
                    mileage INT NOT NULL,
                    fuel_level INT NOT NULL,
                    damage_description TEXT NULL,
                    damage_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                    fuel_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                    cleaned BOOLEAN DEFAULT FALSE,
                    maintenance_required BOOLEAN DEFAULT FALSE,
                    parking_stall_id INT NULL,
                    notes TEXT NULL,
                    FOREIGN KEY (reservation_id) REFERENCES vehicle_reservations(id) ON DELETE CASCADE,
                    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
                    FOREIGN KEY (worker_account_id) REFERENCES accounts(id),
                    FOREIGN KEY (parking_stall_id) REFERENCES parking_stalls(id)
                ) ENGINE=InnoDB
                """;
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private static boolean shouldBootstrap(Connection connection) throws SQLException {
        if (!tableExists(connection, "accounts") || !tableExists(connection, "vehicles")) {
            return true;
        }

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM car_rental_systems")) {
            return !resultSet.next() || resultSet.getInt(1) == 0;
        }
    }

    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getTables(connection.getCatalog(), null, tableName, new String[]{"TABLE"})) {
            return resultSet.next();
        }
    }

    private static void runDataset(Connection connection) throws IOException, SQLException {
        List<String> statements = parseStatements(readDataset());

        try (Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.execute(sql);
            }
        }
    }

    private static String readDataset() throws IOException {
        InputStream inputStream = DatabaseInitializer.class.getResourceAsStream(DATASET_RESOURCE);
        if (inputStream == null) {
            throw new IOException("Dataset resource not found: " + DATASET_RESOURCE);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("--") || trimmed.startsWith("/*") || trimmed.startsWith("*/")) {
                    continue;
                }

                builder.append(line).append('\n');
            }
            return builder.toString();
        }
    }

    private static List<String> parseStatements(String script) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean escaping = false;

        for (int i = 0; i < script.length(); i++) {
            char currentChar = script.charAt(i);

            if (escaping) {
                current.append(currentChar);
                escaping = false;
                continue;
            }

            if (currentChar == '\\' && (inSingleQuote || inDoubleQuote)) {
                current.append(currentChar);
                escaping = true;
                continue;
            }

            if (currentChar == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
                current.append(currentChar);
                continue;
            }

            if (currentChar == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
                current.append(currentChar);
                continue;
            }

            if (currentChar == ';' && !inSingleQuote && !inDoubleQuote) {
                String statement = current.toString().trim();
                if (!statement.isEmpty()) {
                    statements.add(statement);
                }
                current.setLength(0);
                continue;
            }

            current.append(currentChar);
        }

        String trailingStatement = current.toString().trim();
        if (!trailingStatement.isEmpty()) {
            statements.add(trailingStatement);
        }

        return statements;
    }
}
