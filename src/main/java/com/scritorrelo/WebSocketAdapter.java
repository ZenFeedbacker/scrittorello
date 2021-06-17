package com.scritorrelo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neovisionaries.ws.client.WebSocket;

public class WebSocketAdapter extends com.neovisionaries.ws.client.WebSocketAdapter {

    AudioPacket packet = new AudioPacket();

    public void onTextMessage(WebSocket websocket, String message) {
        System.out.println(message);
        JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
        //System.out.println(Integer.toHexString(Integer.parseInt(jsonObject.get("stream_id").toString())));
    }

    public void onBinaryMessage(WebSocket websocket, byte[] binary) {
        AudioFrame audioFrame = new AudioFrame(binary);
        packet.addFrame(audioFrame);
        System.out.println(audioFrame);
        System.out.println(packet);
    }
}
