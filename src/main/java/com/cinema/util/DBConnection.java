package com.cinema.util;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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

        Map<String, String> env = loadEnvFile();
        Properties props = loadClasspathProperties();

        // Priority: .env file > classpath db.properties > defaults
        JDBC_URL = env.getOrDefault("DB_URL", props.getProperty("jdbc.url", "jdbc:oracle:thin:@localhost:1521/ORCL"));
        USERNAME = env.getOrDefault("DB_USERNAME", props.getProperty("jdbc.username", "your_username"));
        PASSWORD = env.getOrDefault("DB_PASSWORD", props.getProperty("jdbc.password", "your_password"));
    }

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    /** Load .env file from the working directory (project root). */
    private static Map<String, String> loadEnvFile() {
        Map<String, String> map = new HashMap<>();
        File envFile = new File(".env");
        if (!envFile.exists()) {
            return map;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(envFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq > 0) {
                    String key = line.substring(0, eq).trim();
                    String value = line.substring(eq + 1).trim();
                    // Remove surrounding quotes if present
                    if (value.length() >= 2 && ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'")))) {
                        value = value.substring(1, value.length() - 1);
                    }
                    map.put(key, value);
                }
            }
        } catch (IOException ignored) {
        }
        return map;
    }

    /** Load db.properties from the classpath as a fallback. */
    private static Properties loadClasspathProperties() {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException ignored) {
        }
        return props;
    }
}
