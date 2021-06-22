package com.scritorrelo;

import com.google.common.primitives.Bytes;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.apache.commons.io.FileUtils;
import org.gagravarr.ogg.OggFile;
import org.gagravarr.ogg.OggPacket;
import org.gagravarr.ogg.OggPacketReader;
import org.gagravarr.ogg.OggPage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.json.Json;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;

@SpringBootApplication
public class EchoClient {
    private static final String SERVER = "wss://zello.io/ws";

    /**
     * The timeout value in milliseconds for socket connection.
     */
    private static final int TIMEOUT = 5000;

    static final String auth_token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJXa002ZW1WdVptVmxaRG94LjNYWXdFaDZoeUlNMk9xR2lBcDB0RjFQWXZIblVJZVBCdWhrNWFpYnZrOGs9IiwiZXhwIjoxNjI2NDQ0NjQ2LCJhenAiOiJkZXYifQ==.Jk8AoJEixXNGbv8k1bHz9m/d6OoiyGc76znd6D5sCuBQYWBghSBcB5EC4TddD+oDOYUIkx6NRRxBGUCPIC/5+msbXs4QHPsw7MVpTZDuloZPPk5KY6VzTxrvyTVnzFolMInMPf8R/VMt11vD8G+ZICC+IDLiuCDB4obIcmsikVvdLIew5Hjm09segEThAOOlzzHhq2cHKsgVgeS9QqtTil7ddC+a4AXT+8oFavpHLwre+NS0xftk33HTVcyKyqprG2jsNZFvcEZeqbPj7A6Igx8oKKwjX8bqjeB2iYjayHcAgs/HHp/kg7RnnIm1iOLriHQe+zMHqmG9ODB+4qGlnA==";


    /**
     * The entry point of this command line application.
     */
    public static void main(String[] args) throws Exception {

        //getDataWithVorbis();
        openFileManually();
        splitFileAsArray();
        //openFileWithVorbis();
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

    private static void splitFileAsArray() throws IOException {
        byte[] file = FileUtils.readFileToByteArray(new File("src/main/resources/speech.opus"));

        int index = 0;
        int subindex;
        List<Integer> indexes = new ArrayList<>();

        while (true) {
            subindex = Bytes.indexOf(Arrays.copyOfRange(file, index, file.length), "OggS".getBytes(StandardCharsets.US_ASCII));
            if (subindex == -1) {
                break;
            }
            indexes.add(index + subindex);
            index += subindex + 3;
        }

        indexes.add(file.length);

        List<byte[]> packets = new ArrayList<>();

        for(int i = 0; i<indexes.size()-1; i++){
            packets.add(Arrays.copyOfRange(file,indexes.get(0),indexes.get(i+1)));
        }

        System.out.println(indexes);
    }

    private static void openFileManually() throws IOException {

        byte[] file = FileUtils.readFileToByteArray(new File("src/main/resources/speech.opus"));
        ByteArrayInputStream fileStream = new ByteArrayInputStream(file);
        com.scritorrelo.OggPage page = new com.scritorrelo.OggPage(fileStream);
        System.out.println(page);
    }

    private static void getDataWithVorbis() throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        OggFile oggFile = new OggFile(new FileInputStream("src/main/resources/speech.opus"));
        OggPacketReader reader = oggFile.getPacketReader();
        OggPacket packet = reader.getNextPacket();
        OggPage page = getPage(packet);
        System.out.println(getSiD(page));
    }

    private static void openFileWithVorbis() throws Exception {

        OggFile oggFile = new OggFile(new FileInputStream("src/main/resources/speech.opus"));
        OggPacketReader reader = oggFile.getPacketReader();
        OggPacket packet = reader.getNextPacket();
        com.scritorrelo.OggPage oggPage = new com.scritorrelo.OggPage(packet.getData());

        System.out.println(oggPage);
        OpusPacketIDHeader header = new OpusPacketIDHeader(packet.getData());
        System.out.println(header);
        OpusPacketCommentHeader commentHeader = new OpusPacketCommentHeader(reader.getNextPacket().getData());
        System.out.println(commentHeader);
        int count = 0;

        while (true) {
            packet = reader.getNextPacket();
            if (isNull(packet)) {
                break;
            }


            byte[] data = packet.getData();
            OpusDataPacket oggPacket = new OpusDataPacket(data);

            count += 1;
            System.out.println("Packet: " + count );
            System.out.println(oggPacket);


        }
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

    private static OggPage getPage(org.gagravarr.ogg.OggPacket packet) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class<? extends OggPacket> packetClass = packet.getClass();
        Method method = packetClass.getDeclaredMethod("_getParent");
        method.setAccessible(true);
        return (OggPage) method.invoke(packet);
    }

    private static Long getChecksum(OggPage oggPage) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class<? extends OggPage> pageClass = oggPage.getClass();
        Method method = pageClass.getDeclaredMethod("getChecksum");
        method.setAccessible(true);
        return (Long) method.invoke(oggPage);
    }

    private static int getSiD(OggPage oggPage) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class<? extends OggPage> pageClass = oggPage.getClass();
        Method method = pageClass.getDeclaredMethod("getSid");
        method.setAccessible(true);
        return (int) method.invoke(oggPage);
    }
}