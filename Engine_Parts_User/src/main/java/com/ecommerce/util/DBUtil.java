package com.ecommerce.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class DBUtil {
    private static final String DB_NAME = "ecommerce.db";
    private static final String DB_PATH = "C:\\Sqllite3_db\\ecommerce.db";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;
    private static final String USER = "";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Database path: " + DB_PATH);
            File dbFile = new File(DB_PATH);
            if (!dbFile.exists()) {
                dbFile.getParentFile().mkdirs();
                // Create the database file by opening a connection
                try (Connection conn = DriverManager.getConnection(URL)) {
                    // Read SQL from resource
                    try (InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("sql/create_tables.sql")) {
                        if (is == null) {
                            throw new RuntimeException("Could not find create_tables.sql in resources");
                        }
                        String sql = new BufferedReader(new InputStreamReader(is))
                                .lines().collect(java.util.stream.Collectors.joining("\n"));
                        try (java.sql.Statement stmt = conn.createStatement()) {
                            for (String s : sql.split(";")) {
                                if (!s.trim().isEmpty()) {
                                    stmt.execute(s);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create or initialize database", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 