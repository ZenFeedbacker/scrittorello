package com.scritorrelo.socket;

import com.neovisionaries.ws.client.*;
import com.scritorrelo.DatabaseManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;

@Slf4j
@Component
class Socket {

    @Value("${:classpath:auth_token}")
    private Resource authTokenFile;

    private static final String SERVER = "wss://zello.io/ws";

    private static final int TIMEOUT = 5000;

    @Getter
    private String channelName;

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
            log.warn("IOException when creating socket for server {}: {}", SERVER, e.getMessage());
            return;
        }

        connect();

        try {
            login();
        } catch (SQLException e) {
            e.printStackTrace();
            SpringApplication.exit(appContext, () -> 0);
        }

        log.info("Socket initialization finished.");
    }

    void connect() {


        try {
            ws.connect();
        } catch (WebSocketException e) {
            log.warn("WebSocketException while socket tried to connect to channel {}: {}", channelName, e.getMessage());
        }
    }

    @PreDestroy
    void disconnect(){

        log.info("Closing connection");
        ws.disconnect();
        log.info("Connection closed");
    }

    void login() throws SQLException {

        var channel = databaseManager.getChannelName();

        channelName = channel.getLeft();

        var loginJson = new JSONObject()
                                .put("command", "logon")
                                .put("seq", 0)
                                .put("auth_token", getToken())
                                .put("channel", channelName)
                                .put("listen_only", "true");

        if(Boolean.TRUE.equals(channel.getRight())){

            var credentials = databaseManager.getCredentials();

            loginJson.put("username", credentials.getLeft());
            loginJson.put("password", credentials.getRight());
        }

        ws.sendText(loginJson.toString());
    }

    private String getToken() {

        return StringUtils.isNullOrEmpty(refreshToken) ? authToken : refreshToken;
    }

    private String getAuthToken(){

        if(authTokenFile == null){
            log.warn("AuthToken field is null");
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
