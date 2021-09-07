package com.scritorrelo.zello.message.audio;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;
import java.util.Arrays;

@Getter
@Slf4j
public class AudioFrame {

    private final byte[] type;
    private final int streamId;
    private final int packetId;
    private final byte[] data;

    public AudioFrame(byte[] binary) {

        type = Arrays.copyOfRange(binary, 0, 1);
        streamId = new BigInteger(Arrays.copyOfRange(binary, 1, 5)).intValue();
        packetId = new BigInteger(Arrays.copyOfRange(binary, 5, 9)).intValue();
        data = Arrays.copyOfRange(binary, 9, binary.length);
    }

    @Override
    public String toString() {

        return "\nType: " + Hex.encodeHexString(type) + "\n" +
                "Stream ID: " + streamId + "\n" +
                "Packet ID: " + packetId + "\n" +
                "Packet length: " + data.length + "\n" +
                "Data: " + Hex.encodeHexString(data) + "\n";
    }
}
