package com.scritorrelo;

import com.neovisionaries.ws.client.WebSocketException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
class WebSocketManager {

    private static final Map<String, ZelloWebSocket> socketMap = new HashMap<>();

    public static final ReentrantLock socketLock = new ReentrantLock();

    @Autowired
    private ObjectFactory<ZelloWebSocket> myBeanFactory;

    @Setter
    @Value("${scrittorello.channels}")
    private String sourceFile;

    @PostConstruct
    private void init() throws Exception {
        log.info("Initializing sockets");

        for(String channel : getChannels()){
            initSocket(channel);
        }
    }

    @PreDestroy
    private void closeAll() {
        log.info("Closing connections");
        socketMap.values().forEach(ZelloWebSocket::disconnect);
        log.info("All connections closed");
    }

    private List<String> getChannels() throws IOException, URISyntaxException {
        URL res = getClass().getClassLoader().getResource(sourceFile);
        return Files.readAllLines(Paths.get(res.toURI()));
    }

    private void initSocket(String channelName) throws WebSocketException, IOException {

        ZelloWebSocket ws = myBeanFactory.getObject();

        ws.setChannelName(channelName);

        socketLock.lock();

        try {
            ws.connect(socketLock);
        } finally {
            socketLock.unlock();
        }

        ws.login();

        socketMap.put(channelName, ws);
    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }
    }
}
