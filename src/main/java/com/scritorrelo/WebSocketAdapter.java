package com.scritorrelo;

import com.neovisionaries.ws.client.WebSocket;
import com.scritorrelo.ogg.OggFile;
import com.scritorrelo.ogg.Stream;
import com.scritorrelo.zello.*;
import com.scritorrelo.zello.Error;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;

public class WebSocketAdapter extends com.neovisionaries.ws.client.WebSocketAdapter {

    final HashMap<Integer, AudioStream> streams = new HashMap<>();

    public void onTextMessage(WebSocket websocket, String message) throws JSONException, IOException {

        LocalDateTime timestamp = LocalDateTime.now();
        JSONObject obj = new JSONObject(message);
        System.out.println(message);


        if (obj.has("command")) {
            String cmnd = obj.getString("command");

            Command command = Command.valueOf(cmnd);

            switch (command) {
                case on_stream_start:
                    AudioStream stream = new AudioStream(obj, timestamp);
                    streams.put(obj.getInt("stream_id"), stream);
                    //System.out.println(stream);
                    break;
                case on_stream_stop:
                    AudioStream audioStream = streams.get(obj.getInt("stream_id"));
                    //  audioStream.toFile();
//                    System.out.println(audioStream);
                    Stream oggStream = new Stream(audioStream.getOpusStream());
                    OggFile oggFile = new OggFile(oggStream);
                    oggFile.writeToFile(Client.outputFile);
                    System.out.println("Wrote file " + Client.outputFile);
                    Client.ws.disconnect();
                    break;
                case on_text_message:
                    Text text = new Text(obj, timestamp);
                    System.out.println(text);
                    break;
                case on_image:
                    Image image = new Image(obj, timestamp);
                    //System.out.println(image);
                    break;
                case on_location:
                    Location location = new Location(obj, timestamp);
                    System.out.println(location);
                    break;
                case on_channel_status:
                    Channel channel = new Channel(obj, timestamp);
                    System.out.println(channel);
                case on_error:
                    Error error = new Error(obj, timestamp);
                case logon:
                default:
                    break;
            }
        }
    }

    public void onBinaryMessage(WebSocket websocket, byte[] binary) {

        switch (binary[0]) {
            case ((byte) 1):
                System.out.println("Received audio binary");
                AudioFrame audioFrame = new AudioFrame(binary);
                streams.get(audioFrame.getStream_id()).addFrame(audioFrame);
                break;
            case ((byte) 2):
                System.out.println("Received image binary");
                ImagePacket image = new ImagePacket(binary);
                System.out.println(image.isThumbnail() ? "Thumbnail" : "Full Image");
                break;
            default:
                System.out.println("Received unknown binary, type " + binary[0]);
                break;
        }
    }
}
