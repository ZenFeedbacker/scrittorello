package com.scritorrelo;

import com.scritorrelo.zello.message.Text;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.sql.*;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

public class DatabaseManager {

    private static final String CREATE_SCHEMA_FILE = "create_schema.sql";
    private static final String DROP_TABLES_FILE = "drops_tables.sql";

    public static void init() throws IOException, SQLException {
        dropTables();
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

    public static void saveMessage(Text text) {
        System.out.println("receive text");
        try (Connection conn = getConnection()) {

            text.getSqlStatement(conn);
            boolean isValid = conn.isValid(0);
            System.out.println("Do we have a valid db connection? = " + isValid);

            UUID uuid = UUID.randomUUID();
            byte[] uuidBytes = new byte[16];
            ByteBuffer.wrap(uuidBytes).order(ByteOrder.BIG_ENDIAN).putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits());

            PreparedStatement statement = conn.prepareStatement("insert into text (uuid, channel,for_User, from_User, id, timestamp, text, test) values (?,?,?,?, ?,?,?,?)");
            statement.setBytes(1, uuidBytes);
            statement.setString(2, "John");
            statement.setString(3, "Rambo");
            statement.setInt(5, 1);
            statement.setString(8, "");
            statement.setString(4, "");
            statement.setString(7, "");
            statement.setTimestamp(6, new Timestamp(System.currentTimeMillis()), Calendar.getInstance(TimeZone.getTimeZone("UTC")));

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
