package com.scritorrelo;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.scritorrelo.ogg.OggFile;
import org.gagravarr.ogg.tools.OggInfoTool;
import org.gagravarr.opus.tools.OpusInfoTool;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.json.Json;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class Client {

    public static final String SERVER = "wss://zello.io/ws";

    public static final int TIMEOUT = 5000;

    public static final WebSocketAdapter adapter = new WebSocketAdapter();

    private static final String auth_token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJXa002ZW1WdVptVmxaRG94LjNYWXdFaDZoeUlNMk9xR2lBcDB0RjFQWXZIblVJZVBCdWhrNWFpYnZrOGs9IiwiZXhwIjoxNjI2NDQ0NjQ2LCJhenAiOiJkZXYifQ==.Jk8AoJEixXNGbv8k1bHz9m/d6OoiyGc76znd6D5sCuBQYWBghSBcB5EC4TddD+oDOYUIkx6NRRxBGUCPIC/5+msbXs4QHPsw7MVpTZDuloZPPk5KY6VzTxrvyTVnzFolMInMPf8R/VMt11vD8G+ZICC+IDLiuCDB4obIcmsikVvdLIew5Hjm09segEThAOOlzzHhq2cHKsgVgeS9QqtTil7ddC+a4AXT+8oFavpHLwre+NS0xftk33HTVcyKyqprG2jsNZFvcEZeqbPj7A6Igx8oKKwjX8bqjeB2iYjayHcAgs/HHp/kg7RnnIm1iOLriHQe+zMHqmG9ODB+4qGlnA==";

    public  static String refresh_token;
    public static final String sampleFile = "src/main/resources/speech.opus";
    public static final String outputFile = "src/main/resources/out.opus";

    public static WebSocket ws;

    public static void main(String[] args) throws Exception {

        //parseFile(sampleFile);

        Files.deleteIfExists(Paths.get(outputFile));

        ws = connect();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        String text;

        String json;

        json = createLogonString("Test1653", 1);

        ws.sendText(json);

//        json = createLogonString("Test143298h", 2);
//
//        ws.sendText(json);


        while ((text = in.readLine()) != null) {
            if (text.equals("exit")) {
                json = createLogonString("Test143298h", 2);

                ws.sendText(json);            }
        }

        ws.disconnect();

        parseFile(outputFile);
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
        return new WebSocketFactory()
                .setConnectionTimeout(TIMEOUT)
                .createSocket(SERVER)
                .addListener(adapter)
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect();
    }
}