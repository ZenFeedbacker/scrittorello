package com.scritorrelo;

import com.neovisionaries.ws.client.WebSocket;
import com.scritorrelo.opus.OpusDataPacket;
import com.scritorrelo.zello.ZelloAudioFrame;
import com.scritorrelo.zello.ZelloAudioPacket;
import org.apache.commons.codec.binary.Hex;

import java.io.EOFException;

public class WebSocketAdapter extends com.neovisionaries.ws.client.WebSocketAdapter {

    final ZelloAudioPacket packet = new ZelloAudioPacket();

    public void onTextMessage(WebSocket websocket, String message) {
        System.out.println(message);
    }

    public void onBinaryMessage(WebSocket websocket, byte[] binary) throws EOFException {
        ZelloAudioFrame audioFrame = new ZelloAudioFrame(binary);

        OpusDataPacket dataPacket = new OpusDataPacket(audioFrame.getData());
        packet.addFrame(audioFrame);
        System.out.println(dataPacket);
    }
}
