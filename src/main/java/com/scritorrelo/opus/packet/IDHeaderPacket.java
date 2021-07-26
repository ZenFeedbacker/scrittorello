package com.scritorrelo.opus.packet;

import com.scritorrelo.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.EOFException;
import java.nio.ByteBuffer;

@Builder
@AllArgsConstructor
public class IDHeaderPacket extends Packet {

    public static final String OPUS_ID_HEADER = "OpusHead";

    private final String signature;
    private final int version;
    private final int channelCount;
    private final short preskip;
    private final int sampleRate;
    private final short outputGain;
    private final int channelMappingFamily;
    private final int streamCount;
    private final int coupleStreamCount;
    private final String channelMapping;

    public IDHeaderPacket(byte[] data) throws EOFException {

        super(data);

        signature = Utils.readByteStreamToString(stream, 8);
        version = Utils.readByteStream(stream);
        channelCount = Utils.readByteStream(stream);
        preskip = (short) Utils.readByteStreamToInt(stream, 2);
        sampleRate = Utils.readByteStreamToInt(stream, 4);
        outputGain =(short) Utils.readByteStreamToInt(stream, 2);
        channelMappingFamily = Utils.readByteStreamToInt(stream);
        if (channelMappingFamily != 0) {
            streamCount = Utils.readByteStreamToInt(stream);
            coupleStreamCount = Utils.readByteStreamToInt(stream);
            channelMapping = Utils.readByteStreamToString(stream, 8 * channelCount);
        } else {
            streamCount = 0;
            coupleStreamCount = 0;
            channelMapping = "";
        }
    }

    @Override
    public byte[] toByteArray() {

        var bb = ByteBuffer.allocate(19);

        bb.put(signature.getBytes());
        bb.put((byte) version);
        bb.put((byte) channelCount);
        bb.putShort(preskip);
        bb.putInt(sampleRate);
        bb.putShort(outputGain);
        bb.put((byte)channelMappingFamily);

        return bb.array();

    }

    @Override
    public String toString() {

        String str = "-------Opus ID Header-------\n" +
                "Length: " + length + "\n" +
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
