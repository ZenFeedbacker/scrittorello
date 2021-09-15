package com.scritorrelo.socket;

import com.scritorrelo.DatabaseManager;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
@Component
public class SocketManager  {

    static final String KEY_COMMAND = "command";

    @Value("${:classpath:auth_token}")
    private Resource authTokenFile;

    @Autowired
    private SocketHandler socketHandler;

    @Autowired
    private DatabaseManager databaseManager;

    private static final String SERVER = "wss://zello.io/ws";

    private String channelName = "Test1653";

    private String username;

    private String password;

    private String userAccount = "false";

    @Setter
    WebSocketSession wsSession;

    @Setter
    private String refreshToken;

    private String authToken;

    private WebSocketConnectionManager wsConnManager;

    @PostConstruct
    private void initManager() {

        log.info("Initializing socket");

        authToken = getAuthToken();

        wsConnManager = new WebSocketConnectionManager(new StandardWebSocketClient(), socketHandler, SERVER);

        wsConnManager.setAutoStartup(true);

        wsConnManager.start();

        log.info("Socket initialization finished.");
    }

    @PreDestroy
    void disconnect() throws IOException {

        log.info("Closing connection");
        wsSession.close();
        log.info("Connection closed");
    }

    void login() {

        //obtainChannelName();

        var loginJson = new JSONObject()
                .put(KEY_COMMAND, "logon")
                .put("seq", 0)
                .put("auth_token", getToken())
                .put("channel", channelName)
                .put("listen_only", "true");

        if ("true".equals(userAccount)) {
            //obtainCredentials();
            loginJson.put("InitializingBeanusername", username);
            loginJson.put("password", password);
        }

        log.info("Send message: {}", loginJson);

        sendMessage(loginJson.toString());
    }

    public void reconnect() {
        log.info("Recreating socket");
        refreshToken = null;
        wsConnManager.stop();
        wsConnManager.start();
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

    private void sendMessage(String message) {

        if (wsSession != null && message != null) {
            try {
                log.debug("Send message: " + message);
                wsSession.sendMessage(new TextMessage(message));
            } catch (Throwable e) {
                log.error("Send message failed: ", e);
            }
        }
    }

    private String getToken() {

        return isEmpty(refreshToken) ? authToken : refreshToken;
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
