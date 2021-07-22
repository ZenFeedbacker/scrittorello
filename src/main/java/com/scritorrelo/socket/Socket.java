package com.scritorrelo.socket;

import com.neovisionaries.ws.client.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import javax.json.Json;
import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
@Service
@Scope("prototype")
class Socket {

    @Value("${:classpath:auth_token}")
    public Resource authTokenFile;

    @Value("${scrittorello.server}")
    public String server;

    @Value("${scrittorello.timeout}")
    public int timeout;

    @Value("${scrittorello.username}")
    public String username;

    @Value("${scrittorello.password}")
    public String password;

    @Autowired
    private ObjectFactory<SocketAdapter> adapterObjectFactory;

    @Setter
    private String refreshToken;

    private String authToken;

    private WebSocket ws;

    @Setter
    @Getter
    private String channelName;

    @PostConstruct
    void init() {

        authToken = getAuthToken();

        SocketAdapter adapter = adapterObjectFactory.getObject();

        try {
            ws = new WebSocketFactory()
                    .setConnectionTimeout(timeout)
                    .createSocket(server)
                    .addListener(adapter)
                    .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);

            adapter.setWs(this);

        } catch (IOException e) {
            log.warn("IOException when creating socket for server {}: {}", server, e.getMessage());
        }

    }

    void connect() {

        SocketManager.socketLock.lock();

        try {
            ws.connect();
        } catch (WebSocketException e) {
            log.warn("WebSocketException while trying to connect to channel {}: {}", channelName, e.getMessage());
        } finally {
            SocketManager.socketLock.unlock();
        }
    }

    void login() {

        String loginMessage = Json.createObjectBuilder()
                .add("command", "logon")
                .add("seq", 0)
                .add("auth_token", getToken())
                .add("channel", channelName)
                .add("listen_only", "true")
//                .add("username", username)
//                .add("password", password)
                .build()
                .toString();

        ws.sendText(loginMessage);
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

    void disconnect(){
        ws.disconnect();
    }

    WebSocketState getState(){

        return ws.getState();
    }
}
