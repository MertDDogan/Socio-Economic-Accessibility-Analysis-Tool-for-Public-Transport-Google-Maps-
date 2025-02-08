package com.transitor.group28;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private Connection connection;
    private static final String databaseURL = "jdbc:mysql://localhost:3306/transitorgr28";
    private static final String username = "root"; //* Change this according to your own username
    private static final String password = "Mertcoco46!!";  //* Change this according to your own password

    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(databaseURL, username, password);
        } catch (ClassNotFoundException ex) {
            System.err.println("Error loading Driver");
            throw new SQLException("Error loading Driver.", ex);
        }
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            synchronized (this) {
                if (connection == null || connection.isClosed()) {
                    connection = DriverManager.getConnection(databaseURL, username, password);
                }
            }
        }
        return connection;
    }
}
