package com.scritorrelo;

import com.scritorrelo.opus.OpusPacketIDHeader;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static java.util.Objects.isNull;

public class OggPage {

    byte[] data;
    final ByteArrayInputStream stream;

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


    public OggPage(byte[] data) {

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

        StringBuilder str = new StringBuilder();

        if (!isNull(data)) {
            str.append("Length: ").append(data.length).append("\n");
        }

        str.append("Signature: ").append(signature).append("\n").
                append("Version: ").append(version).append("\n").
                append("Is Continuation: ").append(continuation).append("\n").
                append("Beginning of Stream: ").append(BoS).append("\n").
                append("End of Stream: ").append(EoS).append("\n").
                append("Granule Position: ").append(granule_position).append("\n").
                append("Bitstream serial number: ").append(bitstream_serial_number).append("\n").
                append("Page Sequence Number: ").append(page_sequence_number).append("\n").
                append("CRC Checksum: ").append(CRC_checksum).append("\n").
                append("Number page segments: ").append(number_page_segments).append("\n");

        for (int i = 0; i < number_page_segments; i++) {
            str.append("Lacing Value #").append(i).append(": ").append(segment_table.get(i)).append("\n");
        }

        return str.toString();
    }
}
