package com.scritorrelo.socket;

import com.scritorrelo.zello.ChannelList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class SocketManager {

    private static final List<Socket> socketMap = new ArrayList<>();

    static final ReentrantLock socketLock = new ReentrantLock();

    @Autowired
    private ObjectFactory<Socket> socketObjectFactory;

    @PostConstruct
    private void init(){
        log.info("Initializing sockets.");

        ChannelList.getChannelNames().forEach(this::initSocket);

        log.info("Socket initialization finished.");
    }

    @PreDestroy
    private void closeAll() {
        log.info("Closing connections");
        socketMap.forEach(Socket::disconnect);
        log.info("All connections closed");
    }

    private void initSocket(String channelName) {

        log.info("Initializing socket for channel {}", ChannelList.getChannelAlias(channelName));

        var ws = socketObjectFactory.getObject();

        ws.setChannelName(channelName);

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
