package com.scritorrelo.socket;

import com.neovisionaries.ws.client.*;
import com.scritorrelo.DatabaseManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
@Component
class Socket {

    @Value("${:classpath:auth_token}")
    private Resource authTokenFile;

    private static final String SERVER = "wss://zello.io/ws";

    private static final int TIMEOUT = 5000;

    @Getter
    private String channelName;

    private String username;

    private String password;

    @Autowired
    private SocketAdapter socketAdapter;

    @Autowired
    private DatabaseManager databaseManager;

    @Autowired
    private ApplicationContext appContext;

    @Setter
    private String refreshToken;

    private String authToken;

    private WebSocket ws;

    @PostConstruct
    void init() {

        log.info("Initializing socket");

        authToken = getAuthToken();

        try {
            ws = new WebSocketFactory()
                    .setConnectionTimeout(TIMEOUT)
                    .createSocket(SERVER)
                    .addListener(socketAdapter)
                    .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);

            socketAdapter.setWs(this);

        } catch (IOException e) {
            log.warn("IOException when creating socket: {}", e.getMessage());
            return;
        }

        connect();

        login();

        log.info("Socket initialization finished.");
    }

    @PreDestroy
    void disconnect() {

        log.info("Closing connection");
        ws.disconnect();
        log.info("Connection closed");
    }

    void recreate() {

        log.info("Recreating socket");

        try {
            ws.recreate().connect();
            login();
        } catch (WebSocketException | IOException e) {
            log.warn("{}} when recreating socket: {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    void connect() {

        log.info("Connecting socket.");
        try {
            ws.connect();
        } catch (WebSocketException e) {
            log.error("WebSocketException while socket tried to connect to channel {}: {}", channelName, e.getMessage());
        }
    }

    void login() {

        obtainChannelName();
        obtainCredentials();

        var loginJson =
                new JSONObject()
                        .put("command", "logon")
                        .put("seq", 0)
                        .put("auth_token", getToken())
                        .put("channel", channelName)
                        .put("listen_only", "true")
                        .put("username", username)
                        .put("password", password);

        ws.sendText(loginJson.toString());
    }

    WebSocketState getState(){
        return ws.getState();
    }

    private String getToken() {

        return isEmpty(refreshToken) ? authToken : refreshToken;
    }

    private void obtainChannelName() {

        log.info("Obtaining channel name.");

        if (isEmpty(channelName)) {
            try {
                channelName = databaseManager.getChannelName();
                log.info("Obtained channel name {}", channelName);
            } catch (SQLException e) {
                log.error("SQLException while obtaining channel Name: {}", e.getMessage());
            }
        }
    }

    private void obtainCredentials() {

        log.info("Obtaining account credentials.");

        if (isEmpty(username) || isEmpty(password)) {
            try {
                var credentials = databaseManager.getCredentials();
                username = credentials.getLeft();
                password = credentials.getRight();
                log.info("Obtained account for user {}.", username);
            } catch (SQLException e) {
                log.error("SQLException while obtaining account credentials: {}", e.getMessage());
            }
        }
    }

    private String getAuthToken() {

        if (authTokenFile == null) {
            log.warn("AuthTokenFile field is null");
            return null;
        }

        try {
            return StreamUtils.copyToString(authTokenFile.getInputStream(), Charset.defaultCharset());
        } catch (IOException e) {
            log.warn("IOException while opening AuthToken file : {}", e.getMessage());
            return null;
        }
    }
}
