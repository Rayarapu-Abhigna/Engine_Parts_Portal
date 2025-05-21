package com.ecommerce.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;


public class DatabaseUtil {
    private static final String DB_PATH = System.getProperty("user.home") + File.separator + "ecommerce.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private static Connection connection = null;

    static {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            File dbFile = new File(DB_PATH);
            if (!dbFile.exists()) {
                dbFile.getParentFile().mkdirs();
                // Create the database file by opening a connection
                try (Connection conn = DriverManager.getConnection(DB_URL)) {
                    // Read SQL from resource
                    try (InputStream is = DatabaseUtil.class.getClassLoader().getResourceAsStream("sql/create_tables.sql")) {
                        if (is == null) {
                            throw new RuntimeException("Could not find create_tables.sql in resources");
                        }
                        String sql = new BufferedReader(new InputStreamReader(is))
                                .lines().collect(java.util.stream.Collectors.joining("\n"));
                        try (Statement stmt = conn.createStatement()) {
                            for (String s : sql.split(";")) {
                                if (!s.trim().isEmpty()) {
                                    stmt.execute(s);
                                }
                            }
                        }
                    }
                }
            }
            if (!dbFile.canWrite() && dbFile.exists()) {
                throw new RuntimeException("Database file is not writable: " + DB_PATH);
            }
            // Initialize database by creating tables (in case of updates)
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create database from SQL script", e);
        }
    }

    private static void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection()) {
            createTables(conn);
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "email TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "phone TEXT UNIQUE NOT NULL," +
                    "name TEXT," +
                    "city TEXT," +
                    "pincode TEXT," +
                    "address TEXT)");

            // Products table
            stmt.execute("CREATE TABLE IF NOT EXISTS products (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "description TEXT," +
                    "price REAL NOT NULL," +
                    "stock INTEGER NOT NULL)");

            // Orders table
            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "user_name TEXT NOT NULL," +
                    "address TEXT NOT NULL," +
                    "city TEXT NOT NULL," +
                    "pincode TEXT NOT NULL," +
                    "phone TEXT NOT NULL," +
                    "total_amount REAL NOT NULL," +
                    "status TEXT DEFAULT 'pending'," +
                    "order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id))");

            // Order items table
            stmt.execute("CREATE TABLE IF NOT EXISTS order_items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "order_id INTEGER NOT NULL," +
                    "product_id INTEGER NOT NULL," +
                    "quantity INTEGER NOT NULL," +
                    "price REAL NOT NULL," +
                    "FOREIGN KEY (order_id) REFERENCES orders(id)," +
                    "FOREIGN KEY (product_id) REFERENCES products(id))");

            // Cart table
            stmt.execute("CREATE TABLE IF NOT EXISTS cart (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "product_id INTEGER NOT NULL," +
                    "quantity INTEGER NOT NULL," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)," +
                    "FOREIGN KEY (product_id) REFERENCES products(id))");

            // Insert default admin user if not exists
            stmt.execute("INSERT OR IGNORE INTO users (email, password, phone, name) " +
                    "VALUES ('admin@gmail.com', 'admin', 'admin', 'Administrator')");
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 