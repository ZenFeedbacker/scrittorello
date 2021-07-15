package com.scritorrelo.socket;

import com.neovisionaries.ws.client.*;
import lombok.Setter;
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

@Service
@Scope("prototype")
class Socket {

    @Value("${:classpath:auth_token}")
    public Resource authToken;

    @Value("${scrittorello.server}")
    public String server;

    @Value("${scrittorello.timeout}")
    public int timeout;

    @Autowired
    private ObjectFactory<SocketAdapter> adapterObjectFactory;

    @Setter
    private String refreshToken;

    private WebSocket ws;

    @Setter
    private String channelName;

    @PostConstruct
    void init() throws IOException {

        SocketAdapter adapter = adapterObjectFactory.getObject();
        ws = new WebSocketFactory()
                .setConnectionTimeout(timeout)
                .createSocket(server)
                .addListener(adapter)
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);

        adapter.setWs(this);
    }

    void connect() throws WebSocketException {
        SocketManager.socketLock.lock();
        try {
            ws.connect();
        } finally {
            SocketManager.socketLock.unlock();
        }
    }

    void login() throws IOException {

        String loginMessage = Json.createObjectBuilder()
                .add("command", "logon")
                .add("seq", 0)
                .add("auth_token", getToken())
                .add("channel", channelName)
                .add("listen_only", "true")
                .build()
                .toString();

        ws.sendText(loginMessage);
    }

    void disconnect(){
        ws.disconnect();
    }

    private String getToken() throws IOException {
        String tokenString = StreamUtils.copyToString(authToken.getInputStream(), Charset.defaultCharset());

        return refreshToken == null ? tokenString : refreshToken;
    }
}
