package com.scritorrelo;

import com.neovisionaries.ws.client.WebSocket;
import com.scritorrelo.ogg.OggFile;
import com.scritorrelo.ogg.Stream;
import com.scritorrelo.zello.Command;
import com.scritorrelo.zello.AudioFrame;
import com.scritorrelo.zello.AudioStream;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
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

            switch (command){
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
                default:
                    break;
            }
        }
    }

    public void onBinaryMessage(WebSocket websocket, byte[] binary) {

        AudioFrame audioFrame = new AudioFrame(binary);
        streams.get(audioFrame.getStream_id()).addFrame(audioFrame);
    }
}
