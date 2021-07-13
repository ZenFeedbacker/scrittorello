package com.scritorrelo;

import com.scritorrelo.zello.message.Message;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.file.Files;
import java.sql.*;

public class DatabaseManager {

    private static final String CREATE_SCHEMA_FILE = "create_schema.sql";
    private static final String DROP_TABLES_FILE = "drops_tables.sql";

    public static void init() throws IOException, SQLException {

        dropTables();
        createTables();
    }

    private static void createTables() throws SQLException, IOException {
        String schema = parseResourceFile(CREATE_SCHEMA_FILE);
        try (Connection conn = getConnection()) {

            Statement stmt = conn.createStatement();
            stmt.execute(schema);
            System.out.println("Table Created......");
        }
    }

    private static void dropTables() throws IOException, SQLException {
        String schema = parseResourceFile(DROP_TABLES_FILE);
        try (Connection conn = getConnection()) {

            Statement stmt = conn.createStatement();
            stmt.execute(schema);
            System.out.println("Tables dropped......");
        }
    }

    private static String parseResourceFile(String path) throws IOException {
        File file = ResourceUtils.getFile("classpath:" + path);
        return new String(Files.readAllBytes(file.toPath()));
    }

    public static void saveMessage(Message message) {
        System.out.println("receive text");
        try (Connection conn = getConnection()) {

            PreparedStatement statement = conn.prepareStatement(message.getSqlStatement());
            int insertedRows = statement.executeUpdate();
            System.out.println("I just inserted " + insertedRows + " users");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:file:./data/db", "sa", "password");
    }
}
