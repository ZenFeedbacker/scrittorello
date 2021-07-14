package com.scritorrelo;

import com.neovisionaries.ws.client.*;
import com.scritorrelo.zello.Channel;
import lombok.Setter;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.json.Json;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.scritorrelo.WebSocketManager.*;

@Service
@Scope("prototype")
class ZelloWebSocket {

    @Setter
    private String refreshToken;

    private WebSocket socket;

    @Setter
    private String channelName;

    @Autowired
    private ObjectFactory<ZelloWebSocketAdapter> adapterObjectFactory;

    private Channel channel;

    @PostConstruct
    void init() throws IOException {

        ZelloWebSocketAdapter adapter = adapterObjectFactory.getObject();
        socket = new WebSocketFactory()
                .setConnectionTimeout(TIMEOUT)
                .createSocket(SERVER)
                .addListener(adapter)
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);

        adapter.setWs(this);
    }

    void connect(ReentrantLock lock) throws WebSocketException {
        lock.lock();
        try {
            socket.connect();
        } finally {
            lock.unlock();
        }
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

    void disconnect(){
        socket.disconnect();
    }

    WebSocketState getState(){
        return socket.getState();
    }

    private String getToken() {
        return refreshToken == null ? AUTH_TOKEN : refreshToken;
    }
}
