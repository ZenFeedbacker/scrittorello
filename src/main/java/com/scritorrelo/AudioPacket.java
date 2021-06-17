package com.scritorrelo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;

import static java.util.Objects.isNull;

@NoArgsConstructor
@Getter
public class AudioPacket {

    int streamID;
    byte[][] data;

    void addFrame(AudioFrame frame) {

        if (isNull(data)) {
            streamID = frame.getStream_id();
            data = new byte[1][1];
            data[0] = Arrays.copyOf(frame.getData(), frame.getData().length);
        } else if (streamID == frame.getStream_id()) {
            data = Arrays.copyOf(data, data.length + 1);
            data[data.length - 1] = frame.getData().clone();
        }
    }

    @Override
    public String toString() {

        StringBuilder string = new StringBuilder();

        string.append("Stream ID: ").append(this.streamID).append("\n");

        for (byte[] row : this.data) {
            string.append(Hex.encodeHex(row)).append("\n");
        }
        return string.toString();
    }
}
