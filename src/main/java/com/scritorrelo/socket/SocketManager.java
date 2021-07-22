package com.scritorrelo.socket;

import com.google.common.base.Joiner;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
class SocketManager {

    private static final Map<String, Socket> socketMap = new HashMap<>();

    public static final ReentrantLock socketLock = new ReentrantLock();

    @Autowired
    private ObjectFactory<Socket> myBeanFactory;

    @Setter
    @Value("${scrittorello.channels}")
    private String sourceFile;

    @PostConstruct
    private void init(){
        log.info("Initializing sockets");

        for (String channel : getChannels()) {
            initSocket(channel);
        }

        printSocketsStatus();
    }

    @PreDestroy
    private void closeAll() {
        log.info("Closing connections");
        socketMap.values().forEach(Socket::disconnect);
        log.info("All connections closed");
    }

    private List<String> getChannels()  {

        URL res = getClass().getClassLoader().getResource(sourceFile);
        try {
            return res == null ? new ArrayList<>() : Files.readAllLines(Paths.get(res.toURI()));
        } catch (IOException | URISyntaxException e) {
            log.error("Failed to open channels' list file: {}", sourceFile);
            return null;
        }
    }

    private void initSocket(String channelName) {

        Socket ws = myBeanFactory.getObject();

        ws.setChannelName(channelName);
        log.info("Initializing socket for channel " + channelName);

        socketLock.lock();

        try {
            ws.connect();
        } finally {
            socketLock.unlock();
        }

        log.info("Logging to channel " + channelName);

        ws.login();

        socketMap.put(channelName, ws);
    }

    private void printSocketsStatus() {

        int created, connecting, open, closing, closed;
        created = connecting = open = closed = closing = 0;

        for (Socket socket : socketMap.values()) {
            switch (socket.getState()) {
                case OPEN:
                    open++;
                    break;
                case CREATED:
                    created++;
                    break;
                case CLOSED:
                    closed++;
                    break;
                case CLOSING:
                    closing++;
                    break;
                case CONNECTING:
                    connecting++;
                    break;
            }
        }

        List<String> states = new ArrayList<>();
        
        if (created > 0) {
            states.add(created + " Created");
        }

        if (connecting > 0) {
            states.add(connecting + " Connecting");
        }

        if (open > 0) {
            states.add(open +" Open");
        }

        if (closing > 0) {
            states.add(closing + " Closing");
        }

        if (closed > 0) {
            states.add(closed + " Closed");
        }

        log.info(socketMap.size() + " Sockets (" + Joiner.on(", ").join(states) + ")");

        socketMap.values().forEach(ws -> log.info("Socket for channel {} is: {}", ws.getChannelName(), ws.getState().toString()));
    }
}
