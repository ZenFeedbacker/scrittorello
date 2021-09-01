package com.scritorrelo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.annotation.PostConstruct;

@Component
public class SocketManager {

    @Autowired
    private SocketHandler socketHandler;

    @Value("${scrittorello.server}")
    private String server;

    @PostConstruct
    private void initManager() {

        var wsConnManager = new WebSocketConnectionManager(new StandardWebSocketClient(), socketHandler, server);

        wsConnManager.setAutoStartup(true);

        wsConnManager.start();
    }
}
