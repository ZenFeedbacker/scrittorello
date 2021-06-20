package com.scritorrelo;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.gagravarr.ogg.OggFile;
import org.gagravarr.ogg.OggPacketReader;
import org.gagravarr.opus.OpusAudioData;
import org.gagravarr.opus.OpusFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.json.Json;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.lwjgl.util.opus.Opus;

import static java.util.Objects.isNull;

@SpringBootApplication
public class EchoClient {
    private static final String SERVER = "wss://zello.io/ws";

    /**
     * The timeout value in milliseconds for socket connection.
     */
    private static final int TIMEOUT = 5000;

    static String auth_token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJXa002ZW1WdVptVmxaRG94LjNYWXdFaDZoeUlNMk9xR2lBcDB0RjFQWXZIblVJZVBCdWhrNWFpYnZrOGs9IiwiZXhwIjoxNjI2NDQ0NjQ2LCJhenAiOiJkZXYifQ==.Jk8AoJEixXNGbv8k1bHz9m/d6OoiyGc76znd6D5sCuBQYWBghSBcB5EC4TddD+oDOYUIkx6NRRxBGUCPIC/5+msbXs4QHPsw7MVpTZDuloZPPk5KY6VzTxrvyTVnzFolMInMPf8R/VMt11vD8G+ZICC+IDLiuCDB4obIcmsikVvdLIew5Hjm09segEThAOOlzzHhq2cHKsgVgeS9QqtTil7ddC+a4AXT+8oFavpHLwre+NS0xftk33HTVcyKyqprG2jsNZFvcEZeqbPj7A6Igx8oKKwjX8bqjeB2iYjayHcAgs/HHp/kg7RnnIm1iOLriHQe+zMHqmG9ODB+4qGlnA==";


    /**
     * The entry point of this command line application.
     */
    public static void main(String[] args) throws Exception {

        /*OpusStream opusStream = new OpusStream("src/main/resources/speech.opus");

        byte [] packet = new byte[0];
        while (!isNull(packet)){
            packet = opusStream.get_next_opus_packet();
        }*/


//        OpusInfoTool info = new OpusInfoTool();
//        info.process(new File("src/main/resources/speech.opus"), true);

        OggFile oggFile = new OggFile(new FileInputStream("src/main/resources/speech.opus"));
        OggPacketReader reader = oggFile.getPacketReader();
        OggPacket packet = new OggPacket(reader.getNextPacket().getData());
        System.out.println(packet.toString());
        System.exit(0);

        // Connect to the echo server.
        WebSocket ws = connect();

        // The standard input via BufferedReader.
        BufferedReader in = getInput();

        // A text read from the standard input.
        String text;

        String json = Json.createObjectBuilder()
                .add("command", "logon")
                .add("seq", 1)
                .add("auth_token", auth_token)
                .add("channel", "Test1653")
                .add("listen_only", "true")
                .build()
                .toString();

        ws.sendText(json);


        while ((text = in.readLine()) != null) {
            if (text.equals("exit")) {
                break;
            }
        }

        ws.disconnect();
    }


    /**
     * Connect to the server.
     */
    private static WebSocket connect() throws Exception {
        return new WebSocketFactory()
                .setConnectionTimeout(TIMEOUT)
                .createSocket(SERVER)
                .addListener(new WebSocketAdapter())
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect();
    }


    /**
     * Wrap the standard input with BufferedReader.
     */
    @NotNull
    @Contract(" -> new")
    private static BufferedReader getInput() {
        return new BufferedReader(new InputStreamReader(System.in));
    }
}