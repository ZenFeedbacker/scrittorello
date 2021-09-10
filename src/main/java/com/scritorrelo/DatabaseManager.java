package com.scritorrelo;

import com.scritorrelo.zello.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;

import static java.util.Objects.isNull;

@Slf4j
@Component
@Configurable
public class DatabaseManager {

    @Value("${spring.datasource.driverClassName}")
    private String dbDriver;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;


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

    public Pair<String, Boolean> getUnusedChannelName() throws SQLException {

        try (var conn = getConnection(); var stmt = conn.createStatement()) {

            var rs = stmt.executeQuery("SELECT * FROM channel WHERE connected = false LIMIT 1");

            if (rs.next()) {
                return new ImmutablePair<>(rs.getString("name"), rs.getBoolean("authentication"));
            } else {
                throw new SQLException("Error retrieving unused channel name");
            }
        }
    }

    public Pair<String, String> getUnusedCredentials() throws SQLException {

        try (var conn = getConnection(); var stmt = conn.createStatement()) {

            var rs = stmt.executeQuery("SELECT * FROM zello_account WHERE used = false LIMIT 1");

            if (rs.next()) {
                return new ImmutablePair<>(rs.getString("username"), rs.getString("password"));
            } else {
                return new ImmutablePair<>("", "");
            }
        }
    }

    private Connection getConnection() throws SQLException {

        return DriverManager.getConnection(jdbcUrl, username, password);
    }
}
