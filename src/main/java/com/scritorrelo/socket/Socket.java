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
import java.util.concurrent.locks.ReentrantLock;

@Service
@Scope("prototype")
class Socket {

    @Value("${:classpath:auth_token}")
    public Resource AUTH_TOKEN;

    @Value("${scrittorello.server}")
    public String SERVER;

    @Value("${scrittorello.timeout}")
    public int TIMEOUT;

    @Autowired
    private ObjectFactory<SocketAdapter> adapterObjectFactory;

    @Setter
    private String refreshToken;

    private WebSocket socket;

    @Setter
    private String channelName;

    @PostConstruct
    void init() throws IOException {

        SocketAdapter adapter = adapterObjectFactory.getObject();
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

    void login() throws IOException {

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

    private String getToken() throws IOException {
        String token_string = StreamUtils.copyToString(AUTH_TOKEN.getInputStream(), Charset.defaultCharset());

        return refreshToken == null ? token_string : refreshToken;
    }
}
