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

    private static final String CREATE_SCHEMA_FILE = "schema.sql";
    private static final String DROP_TABLES_FILE = "drops_tables.sql";

    @PostConstruct
    public void init() throws IOException, SQLException {

       //dropTables();
       createTables();
    }

    public void saveMessage(Message message) {
        try (Connection conn = getConnection()) {

            PreparedStatement statement = message.getSqlStatement(conn);

            statement.executeUpdate();
            log.info("Inserted " + message.getClass().getSimpleName() + " to database: " + statement);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private void createTables() throws SQLException, IOException {
        String schema = parseResourceFile(CREATE_SCHEMA_FILE);
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()){
            stmt.execute(schema);
            log.info("Database tables created");
        }
    }

    private void dropTables() throws IOException, SQLException {
        String schema = parseResourceFile(DROP_TABLES_FILE);
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()){
            stmt.execute(schema);
            log.info("Database tables dropped.");
        }
    }

    private String parseResourceFile(String path) throws IOException {

        File file = ResourceUtils.getFile("classpath:" + path);
        return new String(Files.readAllBytes(file.toPath()));
    }

    private Connection getConnection() throws SQLException {

        return DriverManager.getConnection(jdbcUrl, username, password);
    }
}
