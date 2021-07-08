package com.scritorrelo;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.scritorrelo.zello.Channel;
import lombok.Setter;

import javax.json.Json;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import static com.scritorrelo.WebSocketManager.*;

@Setter
public class ZelloWebSocket {

    private String refreshToken;

    private WebSocket socket;

    private String channelName;

    private ZelloWebSocketAdapter adapter;

    private Channel channel;

    public ZelloWebSocket(String channelName) throws WebSocketException, IOException {

        this();
        this.channelName = channelName;
    }

    public ZelloWebSocket() throws IOException {

        adapter = new ZelloWebSocketAdapter();

        socket = new WebSocketFactory()
                .setConnectionTimeout(TIMEOUT)
                .createSocket(SERVER)
                .addListener(adapter)
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);

        adapter.setWs(this);
    }

    public void connect(ReentrantLock lock) throws WebSocketException {
        lock.lock();
        try {
            socket.connect();
        } finally {
            lock.unlock();
        }
    }

    public void disconnect(){
        socket.disconnect();
    }

    public void login() {

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
