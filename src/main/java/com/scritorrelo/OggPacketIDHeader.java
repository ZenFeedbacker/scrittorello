package com.scritorrelo;

import java.io.ByteArrayInputStream;
import java.io.EOFException;

public class OggPacketIDHeader extends OggPacket{

    String capture_pattern;
    int version;
    int channel_count;
    int preskip;
    int sample_rate;
    int output_gain;
    int channel_mapping_family;
    int stream_count;
    int couple_stream_count;
    String channel_mapping;

    public OggPacketIDHeader(byte[] data) throws EOFException {
        super(data);

        this.data = data;
        System.out.println(data.length);

        this.stream = new ByteArrayInputStream(this.data);

        capture_pattern = super.readByteStreamToString(8);
        version = (int) super.readByteStream();
        channel_count = (int) super.readByteStream();
        preskip = super.readByteStreamToInt(2);
        sample_rate = super.readByteStreamToInt(4);
        output_gain = super.readByteStreamToInt(2);
        channel_mapping_family = super.readByteStreamToInt();
        if (channel_mapping_family != 0) {
            stream_count = super.readByteStreamToInt();
            couple_stream_count = super.readByteStreamToInt();
            channel_mapping = readByteStreamToString(8 * channel_count);
        }
    }

    @Override
    public String toString() {

        String str = "Length: " + this.data.length + "\n" +
                "Capture Pattern: " + this.capture_pattern + "\n" +
                "Version: " + this.version + "\n" +
                "Channel Count: " + this.channel_count + "\n" +
                "Pre-skip: " + this.preskip + "\n" +
                "Sample Rate: " + this.sample_rate + "\n" +
                "Output Gain: " + this.output_gain + "\n" +
                "Channel Mapping Family: " + this.channel_mapping_family + "\n";

        if (channel_mapping_family != 0) {
            str += "Stream Count: " + this.stream_count + "\n" +
                    "Couple Stream Count: " + this.couple_stream_count + "\n" +
                    "Channel Mapping: " + this.channel_mapping + "\n";
        }

        return str;
    }
}
