package com.scritorrelo;

import java.io.EOFException;

public class OggPacketIDHeader extends OggPacket {

    String signature;
    int version;
    int channelCount;
    int preskip;
    int sampleRate;
    int outputGain;
    int channelMappingFamily;
    int streamCount;
    int coupleStreamCount;
    String channelMapping;

    public OggPacketIDHeader(byte[] data) throws EOFException {

        super(data);

        signature = readByteStreamToString(8);
        version = readByteStream();
        channelCount = readByteStream();
        preskip = readByteStreamToInt(2);
        sampleRate = readByteStreamToInt(4);
        outputGain = readByteStreamToInt(2);
        channelMappingFamily = readByteStreamToInt();
        if (channelMappingFamily != 0) {
            streamCount = readByteStreamToInt();
            coupleStreamCount = readByteStreamToInt();
            channelMapping = readByteStreamToString(8 * channelCount);
        }
    }

    @Override
    public String toString() {

        String str = "Length: " + data.length + "\n" +
                "Signature: " + signature + "\n" +
                "Version: " + version + "\n" +
                "Channel Count: " + channelCount + "\n" +
                "Pre-skip: " + preskip + "\n" +
                "Sample Rate: " + sampleRate + "\n" +
                "Output Gain: " + outputGain + "\n" +
                "Channel Mapping Family: " + channelMappingFamily + "\n";

        if (channelMappingFamily != 0) {
            str += "Stream Count: " + streamCount + "\n" +
                    "Couple Stream Count: " + coupleStreamCount + "\n" +
                    "Channel Mapping: " + channelMapping + "\n";
        }

        return str;
    }
}
