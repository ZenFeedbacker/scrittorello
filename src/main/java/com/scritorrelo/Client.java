package com.scritorrelo;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.scritorrelo.ogg.OggFile;
import org.gagravarr.ogg.tools.OggInfoTool;
import org.gagravarr.opus.tools.OpusInfoTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.json.Json;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);


    public static final String SERVER = "wss://zello.io/ws";

    public static final int TIMEOUT = 5000;

    public static final ZelloWebSocketAdapter adapter = new ZelloWebSocketAdapter();

    private static final String auth_token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJXa002ZW1WdVptVmxaRG94LjNYWXdFaDZoeUlNMk9xR2lBcDB0RjFQWXZIblVJZVBCdWhrNWFpYnZrOGs9IiwiZXhwIjoxNjI2NDQ0NjQ2LCJhenAiOiJkZXYifQ==.Jk8AoJEixXNGbv8k1bHz9m/d6OoiyGc76znd6D5sCuBQYWBghSBcB5EC4TddD+oDOYUIkx6NRRxBGUCPIC/5+msbXs4QHPsw7MVpTZDuloZPPk5KY6VzTxrvyTVnzFolMInMPf8R/VMt11vD8G+ZICC+IDLiuCDB4obIcmsikVvdLIew5Hjm09segEThAOOlzzHhq2cHKsgVgeS9QqtTil7ddC+a4AXT+8oFavpHLwre+NS0xftk33HTVcyKyqprG2jsNZFvcEZeqbPj7A6Igx8oKKwjX8bqjeB2iYjayHcAgs/HHp/kg7RnnIm1iOLriHQe+zMHqmG9ODB+4qGlnA==";

    public  static String refresh_token;
    public static final String sampleFile = "src/main/resources/speech.opus";
    public static final String outputFile = "src/main/resources/out.opus";

    public static Map<String, WebSocket> socketMap;

    public static void main(String[] args) throws Exception {

        socketMap = new HashMap<>();

        BufferedReader br = getBufReaderFromFilename("ChannelsList,txt");

        String line;
        int i = 1;

        while((line=br.readLine())!=null){
            logger.info(line);

            WebSocket ws = connect();

            String jsons = createLogonString(line, i);

            ws.sendText(jsons);

            socketMap.put(line, ws);
            Thread.sleep(3000);
            i++;
        }

        socketMap.values().forEach(WebSocket::disconnect);
    }

    public static BufferedReader getBufReaderFromFilename(String filename) throws URISyntaxException, FileNotFoundException {

        File file = getFileFromResource("ChannelsList.txt");

        FileReader fr=new FileReader(file);   //reads the file
        BufferedReader br=new BufferedReader(fr);
        return br;
    }

    private static String createLogonString(String channelName, int seq){

        //String token = refresh_token == null ? auth_token : refresh_token;
        String token = auth_token;
        return  Json.createObjectBuilder()
                .add("command", "logon")
                .add("seq", seq)
                .add("auth_token", token)
                .add("channel", channelName)
                .add("listen_only", "true")
                .build()
                .toString();
    }

    private static void parseFile(String filename) throws IOException {

        OggFile file = new OggFile(filename);

        OpusInfoTool info = new OpusInfoTool();
        //info.process(new File(filename), true);
        OggInfoTool oggInfo = new OggInfoTool(new File(filename));
        oggInfo.printStreamInfo();

        System.exit(0);
    }

    private static WebSocket connect() throws Exception {
        ZelloWebSocketAdapter adapter = new ZelloWebSocketAdapter();
        WebSocket webSocket = new WebSocketFactory()
                .setConnectionTimeout(TIMEOUT)
                .createSocket(SERVER)
                .addListener(adapter)
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect();

        adapter.setWs(webSocket);
        return  webSocket;
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
}