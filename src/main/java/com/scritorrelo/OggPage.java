package com.scritorrelo;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static java.util.Objects.isNull;

public class OggPage {

    byte[] data;
    ByteArrayInputStream stream;


    String signature;
    int version;
    boolean continuation;
    boolean EoS;
    boolean BoS;
    int granule_position;
    int bitstream_serial_number;
    int page_sequence_number;
    int CRC_checksum; //should be long?
    int number_page_segments;
    List<Integer> segment_table;


    public OggPage(byte[] data) throws EOFException {

        this.data = data;
        this.stream = new ByteArrayInputStream(this.data);
    }

    public OggPage(ByteArrayInputStream stream) throws EOFException {

        this.stream = stream;

        segment_table = new ArrayList<>();

        signature = Utils.readByteStreamToString(stream, 4);
        version = Utils.readByteStreamToInt(stream);

        BitSet bits = BitSet.valueOf(new byte[]{Utils.readByteStream(stream)});
        //System.out.println(Utils.bitSetToString(bits));
        continuation = bits.get(1);
        BoS = bits.get(2);
        EoS = bits.get(4);
        granule_position = Utils.readByteStreamToInt(stream, 8);

        bitstream_serial_number = Utils.readByteStreamToInt(stream, 4);
        page_sequence_number = Utils.readByteStreamToInt(stream, 4);
        CRC_checksum = Utils.readByteStreamToInt(stream, 4);
        number_page_segments = Utils.readByteStreamToIntBigEndian(stream);

        for (int i = 0; i < number_page_segments; i++) {
            segment_table.add(Utils.readByteStreamToIntBigEndian(stream));
        }

        OpusPacketIDHeader header = new OpusPacketIDHeader(Utils.readByteStream(stream, segment_table.get(0)));
        System.out.println(header);
    }

    @Override
    public String toString() {

        String str = "";

        if (!isNull(data)) {
            str += "Length: " + data.length + "\n";
        }

        str += "Signature: " + signature + "\n" +
                "Version: " + version + "\n" +
                "Is Continuation: " + continuation + "\n" +
                "Beginning of Stream: " + BoS + "\n" +
                "End of Stream: " + EoS + "\n" +
                "Granule Position: " + granule_position + "\n" +
                "Bitstream serial number: " + bitstream_serial_number + "\n" +
                "Page Sequence Number: " + page_sequence_number + "\n" +
                "CRC Checksum: " + CRC_checksum + "\n" +
                "Number page segments: " + number_page_segments + "\n";

        for (int i = 0; i < number_page_segments; i++) {
            str += "Lacing Value #" + i + ": " + segment_table.get(i) + "\n";
        }

        return str;
    }
}
