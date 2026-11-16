package com.econest.model.dao;

import com.econest.model.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Single place that knows the database location.
 * Shared file: every team member's DAOs call getConnection(). Add your own .sql file to SQL_FILES.
 */
public final class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:econest.db?foreign_keys=on";
    private static final String[] SQL_FILES = {"/sql/product.sql", "/sql/client.sql", "/sql/sales_inventory.sql"};

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /** Creates the tables if they do not exist. Call once at startup. */
    public static void initialize() {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            for (String file : SQL_FILES) {
                for (String sql : readScript(file).split(";")) {
                    if (!sql.isBlank()) {
                        st.execute(sql);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not initialise the database: " + e.getMessage(), e);
        }
    }

    private static String readScript(String resource) {
        try (InputStream in = DatabaseConnection.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new DatabaseException("SQL script not found: " + resource);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DatabaseException("Could not read SQL script " + resource, e);
        }
    }
}
