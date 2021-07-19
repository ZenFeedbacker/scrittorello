package com.scritorrelo.zello.message.audio;

import lombok.Getter;
import org.apache.commons.codec.binary.Hex;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

@Getter
public class AudioFrame implements Serializable {

    private static final long serialVersionUID = -1097437257673310951L;
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

        return "\n" +
                "Type: " + Hex.encodeHexString(type) + "\n" +
                "Stream ID: " + streamId + "\n" +
                "Packet ID: " + packetId + "\n" +
                "Packet length: " + data.length + "\n" +
                "Data: " + Hex.encodeHexString(data) + "\n";
    }
}
