package com.scritorrelo;

import com.neovisionaries.ws.client.WebSocketException;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Component
class WebSocketManager {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketManager.class);

    public static final String SERVER = "wss://zello.io/ws";
    public static final int TIMEOUT = 5000;
    public static final String AUTH_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJXa002ZW1WdVptVmxaRG94LjNYWXdFaDZoeUlNMk9xR2lBcDB0RjFQWXZIblVJZVBCdWhrNWFpYnZrOGs9IiwiZXhwIjoxNjI2NDQ0NjQ2LCJhenAiOiJkZXYifQ==.Jk8AoJEixXNGbv8k1bHz9m/d6OoiyGc76znd6D5sCuBQYWBghSBcB5EC4TddD+oDOYUIkx6NRRxBGUCPIC/5+msbXs4QHPsw7MVpTZDuloZPPk5KY6VzTxrvyTVnzFolMInMPf8R/VMt11vD8G+ZICC+IDLiuCDB4obIcmsikVvdLIew5Hjm09segEThAOOlzzHhq2cHKsgVgeS9QqtTil7ddC+a4AXT+8oFavpHLwre+NS0xftk33HTVcyKyqprG2jsNZFvcEZeqbPj7A6Igx8oKKwjX8bqjeB2iYjayHcAgs/HHp/kg7RnnIm1iOLriHQe+zMHqmG9ODB+4qGlnA==";
    private static final String DEFAULT_SOURCE_FILE = "ChannelsList.txt";

    private static Map<String, ZelloWebSocket> socketMap;

    public static ReentrantLock socketLock;

    @Autowired
    private ObjectFactory<ZelloWebSocket> myBeanFactory;

    @Setter
    private String sourceFile = DEFAULT_SOURCE_FILE;

    @PostConstruct
    void init() throws Exception {
        socketMap = new HashMap<>();

        File file = getFileFromResource(sourceFile);

        FileReader fr = new FileReader(file); //reads the file
        BufferedReader br = new BufferedReader(fr);

        String channel;

        socketLock = new ReentrantLock();

        while ((channel = br.readLine()) != null) {
            initSocket(channel);
        }
    }

    void closeAll() {
        socketMap.values().forEach(ZelloWebSocket::disconnect);
    }

    private void initSocket(String channelName) throws WebSocketException {
        logger.info(channelName);

        ZelloWebSocket ws = myBeanFactory.getObject();

        ws.setChannelName(channelName);
        System.out.println(ws);

        socketLock.lock();

        try {
            System.out.println(ws.getState());
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
