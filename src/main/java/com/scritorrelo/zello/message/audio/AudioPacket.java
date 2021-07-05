package com.scritorrelo.zello.message.audio;

import com.scritorrelo.opus.DataPacket;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@NoArgsConstructor
public class AudioPacket {

    int streamID;
    @Getter
    List<DataPacket> packets;

    public void addFrame(AudioFrame frame) {

        if (isNull(packets)) {
            streamID = frame.getStream_id();
            packets = new ArrayList<>();
            packets.add(new DataPacket(frame.getData()));
        } else if (streamID == frame.getStream_id()) {
            packets.add(new DataPacket(frame.getData()));
        }
    }

    @Override
    public String toString() {

        StringBuilder string = new StringBuilder();

        string.append("Stream ID: ").append(this.streamID).append("\n");

        for (DataPacket packet : this.packets) {
            string.append(Hex.encodeHex(packet.getData())).append("\n");
        }
        return string.toString();
    }
}
