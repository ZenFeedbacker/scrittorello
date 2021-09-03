package com.scritorrelo.socket;

import com.neovisionaries.ws.client.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
@Component
class Socket {

    @Value("${:classpath:auth_token}")
    private Resource authTokenFile;

    @Value("${scrittorello.server}")
    private String server;

    @Value("${scrittorello.timeout}")
    private int timeout;

    @Getter
    @Value("${scrittorello.channelName}")
    private String channelName;

    @Value("${scrittorello.username}")
    private String username;

    @Value("${scrittorello.password}")
    private String password;

    @Value("${scrittorello.userAccount}")
    private String userAccount;

    @Autowired
    private SocketAdapter socketAdapter;

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
                    .setConnectionTimeout(timeout)
                    .createSocket(server)
                    .addListener(socketAdapter)
                    .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);

            socketAdapter.setWs(this);

        } catch (IOException e) {
            log.warn("IOException when creating socket for server {}: {}", server, e.getMessage());
            return;
        }

        connect();
        login();

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

    void login() {

        var loginJson = new JSONObject()
                                .put("command", "logon")
                                .put("seq", 0)
                                .put("auth_token", getToken())
                                .put("channel", channelName)
                                .put("listen_only", "true");

        if("true".equals(userAccount)){
            loginJson.put("username", username);
            loginJson.put("password", password);
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
