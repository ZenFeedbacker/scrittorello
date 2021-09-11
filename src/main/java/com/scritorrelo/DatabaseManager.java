package com.scritorrelo;

import com.scritorrelo.zello.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.sql.*;

import static java.util.Objects.isNull;

@Slf4j
@Component
@Configurable
public class DatabaseManager {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${bar}")
    private int id;

    public void saveMessage(Message message) {

        if (!isNull(message)) {
            try (var conn = getConnection()) {

                var statement = message.getSqlStatement(conn);

                statement.executeUpdate();
                log.info("Inserted {} with UUID {} to database.", message.getClass().getSimpleName(), message.getUuid());
                log.trace(statement.toString());

            } catch (SQLException e) {
                log.warn("SQLException when writing {} message with UUID {} to database: {}", message.getClass().getSimpleName(), message.getUuid(), e.getMessage());
            }
        }
    }

    public Pair<String, Boolean> getChannelName() throws SQLException {

        log.info("Looking for channel name");

        try (var conn = getConnection(); var stmt = conn.createStatement()) {

            var rs = stmt.executeQuery("SELECT * FROM channel WHERE id=" + id);

            if (rs.next()) {
                var name = rs.getString("name");
                var authentication = rs.getBoolean("authentication");

                log.info("Found channel #" + id + ": " + name + (authentication ? ", authentication required." : "."));

                return new ImmutablePair<>(rs.getString("name"), rs.getBoolean("authentication"));
            } else {
                throw new SQLException("Error retrieving channel name.");
            }
        }
    }

    public Pair<String, String> getCredentials() throws SQLException {

        log.info("Authentication required, looking for credentials");

        try (var conn = getConnection(); var stmt = conn.createStatement()) {

            var rs = stmt.executeQuery("SELECT * FROM zello_account WHERE id=" + id);

            if (rs.next()) {

                var uname = rs.getString("username");
                var pword = rs.getString("password");

                log.info("Found unused credentials #{}: {}.", id, uname);

                return new ImmutablePair<>(uname, pword);
            } else {
                throw new SQLException("Error retrieving credentials.");
            }
        }
    }

    private Connection getConnection() throws SQLException {

        return DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
    }
}
