package com.scritorrelo.opus.packet;

import com.scritorrelo.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.EOFException;

@NoArgsConstructor
public abstract class Packet {

    ByteArrayInputStream stream;
    @Getter
    byte[] packetData;
    int length;

    Packet(byte[] data) {

        this.packetData = data;
        this.length = data.length;
        this.stream = new ByteArrayInputStream(data);
    }

    public static Packet packetFactory(byte[] data) throws EOFException {

        String signature = Utils.readByteArrayToString(data, 8);

        if (IDHeaderPacket.OPUS_ID_HEADER.equals(signature)) {
            return new IDHeaderPacket(data);
        } else if (CommentHeaderPacket.OPUS_COMMENT_HEADER.equals(signature)) {
            return new CommentHeaderPacket(data);
        } else {
            return new DataPacket(data);
        }
    }

    public abstract byte[] toByteArray();

    @Override
    public String toString() {

        return "Length: " + this.length + "\n";
    }
}
