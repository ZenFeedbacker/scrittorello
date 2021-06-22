package com.scritorrelo;

import lombok.Getter;
import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;
import java.util.Arrays;

@Getter
public class AudioFrame {

    final byte[] type;
    final int stream_id;
    final int packet_id;
    final byte[] data;


    public AudioFrame(byte[] binary) {
        type = Arrays.copyOfRange(binary, 0, 1);
        stream_id = new BigInteger(Arrays.copyOfRange(binary, 1, 5)).intValue();
        packet_id = new BigInteger(Arrays.copyOfRange(binary, 5, 9)).intValue();
        data = Arrays.copyOfRange(binary, 9, binary.length);
    }

    @Override
    public String toString() {

        return "\n" +
                "Type: " + Hex.encodeHexString(type) + "\n" +
                "Stream ID: " + stream_id + "\n" +
                "Packet ID: " + packet_id + "\n" +
                "Packet length: " + data.length + "\n" +
                "Data: " + Hex.encodeHexString(data) + "\n";
    }
}
