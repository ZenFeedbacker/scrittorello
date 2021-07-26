package com.scritorrelo.opus.packet;

import com.scritorrelo.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.EOFException;

@Slf4j
@NoArgsConstructor
public abstract class Packet {

    ByteArrayInputStream stream;
    @Getter
    byte[] packetData;
    int length;

    Packet(byte[] data) {

        packetData = data;
        length = data.length;
        stream = new ByteArrayInputStream(data);
    }

    public static Packet packetFactory(byte[] data) {

        var signature = Utils.readByteArrayToString(data, 8);

        if (IDHeaderPacket.OPUS_ID_HEADER.equals(signature)) {
            try {
                return new IDHeaderPacket(data);
            } catch (EOFException e) {
                log.warn("EOFException while trying to create IDHeaderPacket: {}", e.getMessage());
                return null;
            }
        } else if (CommentHeaderPacket.OPUS_COMMENT_HEADER.equals(signature)) {
            try {
                return new CommentHeaderPacket(data);
            } catch (EOFException e) {
                log.warn("EOFException while trying to create CommentHeaderPacket: {}", e.getMessage());
                return null;            }
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
