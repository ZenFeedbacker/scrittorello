package com.scritorrelo.zello;

import com.scritorrelo.opus.OpusDataPacket;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Hex;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@NoArgsConstructor
public class ZelloAudioPacket {

    int streamID;
    @Getter
    List<OpusDataPacket> packets;

    public void addFrame(ZelloAudioFrame frame) throws EOFException {

        if (isNull(packets)) {
            streamID = frame.getStream_id();
            packets = new ArrayList<>();
            packets.add(new OpusDataPacket(frame.getData()));
        } else if (streamID == frame.getStream_id()) {
            packets.add(new OpusDataPacket(frame.getData()));
        }
    }

    @Override
    public String toString() {

        StringBuilder string = new StringBuilder();

        string.append("Stream ID: ").append(this.streamID).append("\n");

        for (OpusDataPacket packet : this.packets) {
            string.append(Hex.encodeHex(packet.getData())).append("\n");
        }
        return string.toString();
    }
}
