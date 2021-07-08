package com.scritorrelo;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebSocketManager {


    private static final Logger logger = LoggerFactory.getLogger(WebSocketManager.class);

    public static final String SERVER = "wss://zello.io/ws";
    public static final int TIMEOUT = 5000;
    public static final String AUTH_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJXa002ZW1WdVptVmxaRG94LjNYWXdFaDZoeUlNMk9xR2lBcDB0RjFQWXZIblVJZVBCdWhrNWFpYnZrOGs9IiwiZXhwIjoxNjI2NDQ0NjQ2LCJhenAiOiJkZXYifQ==.Jk8AoJEixXNGbv8k1bHz9m/d6OoiyGc76znd6D5sCuBQYWBghSBcB5EC4TddD+oDOYUIkx6NRRxBGUCPIC/5+msbXs4QHPsw7MVpTZDuloZPPk5KY6VzTxrvyTVnzFolMInMPf8R/VMt11vD8G+ZICC+IDLiuCDB4obIcmsikVvdLIew5Hjm09segEThAOOlzzHhq2cHKsgVgeS9QqtTil7ddC+a4AXT+8oFavpHLwre+NS0xftk33HTVcyKyqprG2jsNZFvcEZeqbPj7A6Igx8oKKwjX8bqjeB2iYjayHcAgs/HHp/kg7RnnIm1iOLriHQe+zMHqmG9ODB+4qGlnA==";
    private static final String DEFAULT_SOURCE_FILE = "ChannelsList.txt";

    private static WebSocketManager manager;
    private static Map<String, ZelloWebSocket> socketMap;

    @Setter
    private String sourceFile = DEFAULT_SOURCE_FILE;

    public static WebSocketManager getInstance(){

        if(manager == null) {
            manager = new WebSocketManager();
        }
        return manager;
    }

    public void init() throws Exception {
        socketMap = new HashMap<>();

        File file = getFileFromResource("ChannelsList.txt");

        FileReader fr=new FileReader(file); //reads the file
        BufferedReader br = new BufferedReader(fr);

        String channel;

        while((channel=br.readLine())!=null){
            logger.info(channel);

            ZelloWebSocket ws = new ZelloWebSocket(channel);
            ws.connect();
            ws.login();


            Thread.sleep(3000);

            socketMap.put(channel, ws);
        }
    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {

            // failed if files have whitespaces or special characters
            //return new File(resource.getFile());

            return new File(resource.toURI());
        }

    }

    public void closeAll(){
        socketMap.values().forEach(ZelloWebSocket::disconnect);
    }
}
