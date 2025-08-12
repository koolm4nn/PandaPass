package com.pandaPass.persistence;

import com.pandaPass.configs.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides a utility method to establish a connection to the app's database
 * using credentials and settings defined in {@link DatabaseConfig}
 */
public class DB {
    /**
     * Establishes and returns a new database connection.
     * @return a valid {@link Connection} or null if the connection failed.
     */
    public static Connection connect(){
        try{
            // Get credentials from config
            String jdbcUrl = DatabaseConfig.getDbUrl();
            String user = DatabaseConfig.getDbUsername();
            String password = DatabaseConfig.getDbPassword();

            // Establish and return a connection
            return DriverManager.getConnection(jdbcUrl, user, password);
        } catch (SQLException e){
            System.err.println(e.getMessage());
            throw new RuntimeException("Connection to database could not be established.");
        }
    }
}
