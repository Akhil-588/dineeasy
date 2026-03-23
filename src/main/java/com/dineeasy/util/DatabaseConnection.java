package com.dineeasy.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton utility for managing the shared MySQL JDBC connection.
 * Update URL, USERNAME, and PASSWORD to match your local setup.
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/dineeasy?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private static Connection connection = null;

    private DatabaseConnection() {}

    /**
     * Returns the shared connection, creating it on first call.
     *
     * @return active {@link Connection}
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-java to the classpath.", e);
            }
        }
        return connection;
    }

    /** Closes the shared connection gracefully. */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
