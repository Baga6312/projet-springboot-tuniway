


package com.tuniway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DatabaseInitializer {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Bean(name = "databaseCreator")
    public Boolean createDatabaseIfNotExists() {
        // Extract database name from URL
        String databaseName = extractDatabaseName(dbUrl);
        
        // Create connection URL without database name
        String serverUrl = dbUrl.substring(0, dbUrl.lastIndexOf('/'));
        
        try {
            // Connect to MySQL server (without specific database)
            DriverManagerDataSource tempDataSource = new DriverManagerDataSource();
            tempDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            tempDataSource.setUrl(serverUrl + "?allowPublicKeyRetrieval=true&useSSL=false");
            tempDataSource.setUsername(dbUsername);
            tempDataSource.setPassword(dbPassword);
            
            try (Connection connection = tempDataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                
                // Create database if it doesn't exist
                String createDbQuery = "CREATE DATABASE IF NOT EXISTS " + databaseName + 
                                     " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
                statement.executeUpdate(createDbQuery);
                
                System.out.println("✅ Database '" + databaseName + "' created or already exists");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private String extractDatabaseName(String jdbcUrl) {
        // Extract database name from jdbc:mysql://host:port/dbname?params
        String[] parts = jdbcUrl.split("/");
        String lastPart = parts[parts.length - 1];
        
        // Remove parameters if they exist
        if (lastPart.contains("?")) {
            return lastPart.substring(0, lastPart.indexOf("?"));
        }
        
        return lastPart;
    }
}