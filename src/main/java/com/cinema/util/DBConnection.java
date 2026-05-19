package com.cinema.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static final String JDBC_URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Oracle JDBC Driver not found.", e);
        }

        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException ignored) {
        }

        JDBC_URL = props.getProperty("jdbc.url", "jdbc:oracle:thin:@localhost:1521/ORCL");
        USERNAME = props.getProperty("jdbc.username", "your_username");
        PASSWORD = props.getProperty("jdbc.password", "your_password");
    }

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }
}
