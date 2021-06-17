package com.scritorrelo;

import org.apache.commons.lang3.ArrayUtils;

import static java.util.Objects.isNull;

public class AudioPacket {

    int streamID;
    byte[] data;

    void addFrame(AudioFrame frame) {

        if (isNull(data)) {
            streamID = frame.getStream_id();
            data = frame.getData().clone();
        } else if (streamID == frame.getStream_id()) {
            data = ArrayUtils.addAll(data, frame.getData());
        }
    }
}
