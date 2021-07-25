package com.scritorrelo.socket;

import com.neovisionaries.ws.client.*;
import com.scritorrelo.zello.ChannelList;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
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

    @Value("${scrittorello.userAccount}")
    public String userAccount;

    @Autowired
    private ObjectFactory<SocketAdapter> adapterObjectFactory;

    @Autowired
    private SocketManager socketManager;

    @Setter
    private String refreshToken;

    private String authToken;

    private WebSocket ws;

    @Setter @Getter
    private int sn;

    @Setter
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
            log.warn("IOException when creating socket {} for server {}: {}", sn, server, e.getMessage());
        }

    }

    void connect() {

        SocketManager.socketLock.lock();

        try {
            ws.connect();
        } catch (WebSocketException e) {
            log.warn("WebSocketException while socket {} tried to connect to channel {}: {}", sn, channelName, e.getMessage());
        } finally {
            SocketManager.socketLock.unlock();
        }
    }

    void reconnect(){
        try {
            log.info("[Socket {}] Reconnecting to channel {}", sn, channelName);
            ws = ws.recreate().connect();
        } catch (WebSocketException | IOException e) {
            log.warn("WebSocketException while socket {} tried to reconnect to channel {}: {}", sn, channelName, e.getMessage());
        }
    }

    void login() {

        JSONObject loginJson = new JSONObject()
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

    void disconnect(){
        ws.disconnect();
    }

    WebSocketState getState(){

        return ws.getState();
    }

    public Object getChannelName() {

        return ChannelList.getChannelAlias(channelName);
    }
}
