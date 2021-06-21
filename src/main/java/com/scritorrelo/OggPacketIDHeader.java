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

        signature = Utils.readByteStreamToString(stream, 8);
        version = Utils.readByteStream(stream);
        channelCount = Utils.readByteStream(stream);
        preskip = Utils.readByteStreamToInt(stream, 2);
        sampleRate = Utils.readByteStreamToInt(stream, 4);
        outputGain = Utils.readByteStreamToInt(stream, 2);
        channelMappingFamily = Utils.readByteStreamToInt(stream);
        if (channelMappingFamily != 0) {
            streamCount = Utils.readByteStreamToInt(stream);
            coupleStreamCount = Utils.readByteStreamToInt(stream);
            channelMapping = Utils.readByteStreamToString(stream, 8 * channelCount);
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
