package com.scritorrelo;

import com.scritorrelo.zello.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.sql.*;

@Component
@Slf4j
public class DatabaseManager {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private static final String SCHEMA_FILE = "schema.sql";

    @PostConstruct
    public void init(){

       createTables();
    }

    public void saveMessage(Message message) {

        try (var conn = getConnection()) {

            var statement = message.getSqlStatement(conn);

            statement.executeUpdate();
            log.info("Inserted " + message.getClass().getSimpleName() + " to database: " + statement);
        } catch (SQLException e) {
            log.warn("SQLException when writing {} message with UUID {} to database: {}", message.getClass().getSimpleName(), message.getUuid(), e.getMessage());
        }

    }

    private void createTables() {

        String schema;

        try {
            schema = parseResourceFile();
        } catch (IOException e) {
            log.error("IOException while parsing schema file {}: {}", SCHEMA_FILE, e.getMessage());
            return;
        }

        try (var conn = getConnection();
             var stmt = conn.createStatement()){

            stmt.execute(schema);
            log.info("Database tables created");
        } catch (SQLException e) {
            log.error("SQLException while initializing tables: {}", e.getMessage());
        }
    }

    private String parseResourceFile() throws IOException {

        var file = ResourceUtils.getFile("classpath:" + SCHEMA_FILE);
        return new String(Files.readAllBytes(file.toPath()));
    }

    private Connection getConnection() throws SQLException {

        return DriverManager.getConnection(jdbcUrl, username, password);
    }
}
