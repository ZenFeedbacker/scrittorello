package com.scritorrelo;

import com.neovisionaries.ws.client.*;
import com.scritorrelo.zello.Channel;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.json.Json;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.scritorrelo.WebSocketManager.*;

@Setter
@Component
@Scope("prototype")
class ZelloWebSocket {

    private String refreshToken;

    private WebSocket socket;

    private String channelName;

    @Autowired
    private ZelloWebSocketAdapter adapter;

    private Channel channel;

    @PostConstruct
    void init() throws IOException {
        socket = new WebSocketFactory()
                .setConnectionTimeout(TIMEOUT)
                .createSocket(SERVER)
                .addListener(adapter)
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);

        this.adapter.setWs(this);
    }

    void connect(ReentrantLock lock) throws WebSocketException {
        lock.lock();
        try {
            TimeUnit.SECONDS.sleep(3);

            socket.connect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    WebSocketState getState(){
        return socket.getState();
    }

    void disconnect(){
        socket.disconnect();
    }

    void login() {

        String loginMessage = Json.createObjectBuilder()
                .add("command", "logon")
                .add("seq", 0)
                .add("auth_token", getToken())
                .add("channel", channelName)
                .add("listen_only", "true")
                .build()
                .toString();

        socket.sendText(loginMessage);
    }

    private String getToken() {
        return refreshToken == null ? AUTH_TOKEN : refreshToken;
    }
}
