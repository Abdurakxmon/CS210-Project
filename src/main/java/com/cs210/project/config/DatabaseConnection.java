package com.cs210.project.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection class to manage JDBC connections to MySQL.
 * Designed for a CS210 project level.
 */
public class DatabaseConnection {

    public static final String HOST = "localhost";
    public static final int PORT = 3306;
    public static final String DATABASE_NAME = "car_rental_system";
    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE_NAME
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String SERVER_URL =
            "jdbc:mysql://" + HOST + ":" + PORT
                    + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found!");
            e.printStackTrace();
        }
    }

    /**
     * Get a connection to the database.
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static Connection getServerConnection() throws SQLException {
        return DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
    }
}
