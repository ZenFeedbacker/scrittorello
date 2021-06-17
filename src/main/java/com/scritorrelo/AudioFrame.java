package com.scritorrelo;

import lombok.Getter;
import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;
import java.util.Arrays;

@Getter
public class AudioFrame {

    byte[] type;
    byte[] stream_id;
    byte[] packet_id;
    byte[] data;

    int streamid;
    int packetid;

    public AudioFrame(byte[] binary) {
        type = Arrays.copyOfRange(binary, 0, 1);
        stream_id = Arrays.copyOfRange(binary, 1, 5);
        packet_id = Arrays.copyOfRange(binary, 5, 9);
        data = Arrays.copyOfRange(binary, 9, binary.length);

        streamid = new BigInteger(stream_id).intValue();
        packetid = new BigInteger(packet_id).intValue();
    }

    @Override
    public String toString() {

        return "\n" +
                "Type: " + Hex.encodeHexString(type) + "\n" +
                "Stream ID: " + streamid + "\n" +
                "Packet ID: " + packetid + "\n" +
                "Data: " + Hex.encodeHexString(data) + "\n" +
                "Packet length: " + data.length + "\n";
    }
}
