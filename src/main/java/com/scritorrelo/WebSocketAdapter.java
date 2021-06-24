package com.scritorrelo;

import com.neovisionaries.ws.client.WebSocket;
import com.scritorrelo.zello.ZelloAudioFrame;
import com.scritorrelo.zello.ZelloAudioPacket;

public class WebSocketAdapter extends com.neovisionaries.ws.client.WebSocketAdapter {

    final ZelloAudioPacket packet = new ZelloAudioPacket();

    public void onTextMessage(WebSocket websocket, String message) {
        System.out.println(message);
    }

    public void onBinaryMessage(WebSocket websocket, byte[] binary)  {
        ZelloAudioFrame audioFrame = new ZelloAudioFrame(binary);
        packet.addFrame(audioFrame);
        System.out.println(audioFrame);
    }
}
