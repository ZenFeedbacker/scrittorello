package com.scritorrelo.db;

import com.scritorrelo.zello.message.Message;
import com.scritorrelo.zello.message.audio.Audio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.List;

@Slf4j
@Component
public class DatabaseManager {

    private static final String SCHEMA_FILE = "schema.sql";

    @Value("${spring.datasource.driverClassName}")
    private String dbDriver;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @PostConstruct
    public void init() {

        createTables();
    }

    public void saveMessage(Message message) {

        try (var conn = getConnection()) {

            var statement = message.getSqlStatement(conn);

            statement.executeUpdate();
            log.info("Inserted " + message.getClass().getSimpleName() + " to database: ");
            log.trace(statement.toString());
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
             var stmt = conn.createStatement()) {

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

    public DataSource getDataSource() {

        var ds = new DriverManagerDataSource(jdbcUrl, username, password);
        ds.setDriverClassName(dbDriver);
        return ds;
    }
}
