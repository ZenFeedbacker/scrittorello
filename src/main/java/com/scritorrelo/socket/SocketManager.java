package com.scritorrelo.socket;

import com.neovisionaries.ws.client.WebSocketState;
import com.scritorrelo.zello.ChannelList;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SocketManager {

    private static final List<Socket> socketMap = new ArrayList<>();

    static final ReentrantLock socketLock = new ReentrantLock();

    @Autowired
    private ObjectFactory<Socket> socketObjectFactory;

    @Setter
    @Value("${scrittorello.channels}")
    private String sourceFile;

    @Value("${scrittorello.channelAliasing}")
    private boolean channelAliasing;


    @PostConstruct
    private void init(){
        log.info("Initializing sockets.");

        for (String channel : ChannelList.getChannelNames()) {
            initSocket(channel);
        }

        log.info("Socket initialization finished.");
    }

    @PreDestroy
    private void closeAll() {
        log.info("Closing connections");
        socketMap.forEach(Socket::disconnect);
        log.info("All connections closed");
    }

    private void initSocket(String channelName) {

        int sn = socketMap.size();

        log.info("Initializing socket {} for channel {}", sn, ChannelList.getChannelAlias(channelName));

        Socket ws = socketObjectFactory.getObject();

        ws.setChannelName(channelName);
        ws.setSn(sn);

        socketLock.lock();

        try {
            ws.connect();
        } finally {
            socketLock.unlock();
        }

        log.info("Logging to channel {}", ChannelList.getChannelAlias(channelName));

        ws.login();
        
        socketMap.add(ws);
    }
}
