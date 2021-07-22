package com.scritorrelo.socket;

import com.google.common.base.Joiner;
import com.neovisionaries.ws.client.WebSocketState;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
@Component
class SocketManager {

    private static final List<Socket> socketMap = new ArrayList<>();

    public static final ReentrantLock socketLock = new ReentrantLock();

    @Autowired
    private ObjectFactory<Socket> socketObjectFactory;

    @Setter
    @Value("${scrittorello.channels}")
    private String sourceFile;

    @PostConstruct
    private void init(){
        log.info("Initializing sockets.");

        for (String channel : getChannels()) {
            initSocket(channel);
        }

        log.info("Socket initialization finished.");

        printSocketsStatus();
    }

    @PreDestroy
    private void closeAll() {
        log.info("Closing connections");
        socketMap.forEach(Socket::disconnect);
        log.info("All connections closed");
    }

    @Scheduled(fixedDelayString = "${scrittorello.reconnectionDelay}", initialDelayString = "${scrittorello.reconnectionDelay}")
    public void run() {

        printSocketsStatus();

        List<Socket> closedSockets = getClosedSockets();

        if(!closedSockets.isEmpty()) {
            log.info("Trying to reconnect to empty sockets.");
            closedSockets.forEach(Socket::reconnect);
        }
    }

    private List<String> getChannels() {

        URL res = getClass().getClassLoader().getResource(sourceFile);

        try {
            return res == null ? new ArrayList<>() : Files.readAllLines(Paths.get(res.toURI()));
        } catch (IOException | URISyntaxException e) {
            log.error("Failed to open channels' list file: {}", sourceFile);
            return new ArrayList<>();
        }
    }

    private void initSocket(String channelName) {

        int sn = socketMap.size();

        log.info("Initializing socket {} for channel {}", sn, channelName);

        Socket ws = socketObjectFactory.getObject();

        ws.setChannelName(channelName);
        ws.setSn(sn);

        socketLock.lock();

        try {
            ws.connect();
        } finally {
            socketLock.unlock();
        }

        log.info("Logging to channel {}", channelName);

        ws.login();
        
        socketMap.add(ws);
    }

    private void printSocketsStatus() {

        int created, connecting, open, closing, closed;
        created = connecting = open = closed = closing = 0;

        for (Socket socket : socketMap) {
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

        log.info("{} Sockets ({})", socketMap.size(), Joiner.on(", ").join(states));

        socketMap.forEach(ws -> log.info("Socket {} for channel {} is: {}", ws.getSn(), ws.getChannelName(), ws.getState().toString()));
    }

    private List<Socket> getClosedSockets(){
        return socketMap.stream().filter(ws -> ws.getState() == WebSocketState.CLOSED).collect(Collectors.toList());
    }
}
