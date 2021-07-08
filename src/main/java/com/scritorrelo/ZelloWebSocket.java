package com.scritorrelo;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import lombok.Setter;

import javax.json.Json;
import java.io.IOException;

import static com.scritorrelo.WebSocketManager.*;

@Setter
public class ZelloWebSocket {

    private String refreshToken;

    private WebSocket socket;

    private String channel;

    private ZelloWebSocketAdapter adapter;

    public ZelloWebSocket(String channel) throws WebSocketException, IOException {
        this();
        this.channel = channel;

    }

    public ZelloWebSocket() throws IOException, WebSocketException {

        adapter = new ZelloWebSocketAdapter();

        socket = new WebSocketFactory()
                .setConnectionTimeout(TIMEOUT)
                .createSocket(SERVER)
                .addListener(adapter)
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);

        adapter.setWs(this);
    }

    public void connect() throws WebSocketException {
        socket.connect();
    }

    public void disconnect(){
        socket.disconnect();
    }

    public void login() {

        String loginMessage = Json.createObjectBuilder()
                .add("command", "logon")
                .add("seq", 0)
                .add("auth_token", getToken())
                .add("channel", channel)
                .add("listen_only", "true")
                .build()
                .toString();

        socket.sendText(loginMessage);
    }

    private String getToken() {
        return refreshToken == null ? AUTH_TOKEN : refreshToken;
    }
}
