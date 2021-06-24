package com.scritorrelo;

import com.neovisionaries.ws.client.WebSocket;
import com.scritorrelo.zello.Command;
import com.scritorrelo.zello.ZelloAudioFrame;
import com.scritorrelo.zello.ZelloAudioStream;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.time.LocalDateTime;
import java.util.HashMap;

public class WebSocketAdapter extends com.neovisionaries.ws.client.WebSocketAdapter {

    HashMap<Integer, ZelloAudioStream> streams = new HashMap<>();

    public void onTextMessage(WebSocket websocket, String message) throws JSONException {
        LocalDateTime timestamp = LocalDateTime.now();
        JSONObject obj = new JSONObject(message);
        System.out.println(message);

        if (obj.has("command")) {
            String cmnd = obj.getString("command");

            Command command = Command.valueOf(cmnd);

            switch (command){
                case on_stream_start:
                    ZelloAudioStream stream = new ZelloAudioStream(obj, timestamp);
                    streams.put(obj.getInt("stream_id"), stream);
                    System.out.println(stream);
                    break;
                case on_stream_stop:
                    System.out.println(streams.get(obj.getInt("stream_id")));
                    break;
                default:
                    break;

            }
        }
    }

    public void onBinaryMessage(WebSocket websocket, byte[] binary) throws EOFException {
        ZelloAudioFrame audioFrame = new ZelloAudioFrame(binary);

        streams.get(audioFrame.getStream_id()).addFrame(audioFrame);
    }
}
