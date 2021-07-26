package com.scritorrelo.ogg;

import com.scritorrelo.Utils;
import com.scritorrelo.opus.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.zip.CRC32;

@Slf4j
@Builder
@AllArgsConstructor
public class OggPage {

    static final String OGG_PAGE_HEADER = "OggS";

    private static final int MIN_HEADER_SIZE = 27;

    private final ByteArrayInputStream stream;

    @Builder.Default
    private String signature = OGG_PAGE_HEADER;
    @Builder.Default
    private int version = 0;
    @Builder.Default
    private boolean continuation = false;
    @Setter
    @Builder.Default
    private boolean eos = false;
    @Setter
    @Builder.Default
    private boolean bos = false;
    @Builder.Default
    private int granulePosition = 0;
    private int bitstreamSerialNumber;
    @Builder.Default
    private int pageSequenceNumber = 0;
    private int checksum;
    @Builder.Default
    private  int numberPageSegments = 0;
    private final List<Integer> segmentTable;
    private final List<Packet> packets;

    public OggPage(byte[] data) {

        this.stream = new ByteArrayInputStream(data);

        segmentTable = new ArrayList<>();
        packets = new ArrayList<>();

        try {
            signature = Utils.readByteStreamToString(stream, 4);
            version = Utils.readByteStreamToInt(stream);

            BitSet bits = BitSet.valueOf(new byte[]{Utils.readByteStream(stream)});
            continuation = bits.get(0);
            bos = bits.get(1);
            eos = bits.get(2);
            granulePosition = Utils.readByteStreamToInt(stream, 8);

            bitstreamSerialNumber = Utils.readByteStreamToInt(stream, 4);
            pageSequenceNumber = Utils.readByteStreamToInt(stream, 4);
            checksum = Utils.readByteStreamToInt(stream, 4);
            numberPageSegments = Utils.readByteStreamToIntBigEndian(stream);

            for (int i = 0; i < numberPageSegments; i++) {
                segmentTable.add(Utils.readByteToIntBigEndian(stream));
            }

            for (int segment : segmentTable) {
                if (segment != 0) {
                    Packet packet = Packet.packetFactory(Utils.readByteStream(stream, segment));
                    packets.add(packet);
                }
            }

        } catch (EOFException e) {
            e.printStackTrace();
        }
    }

    private byte[] getHeader() {

        byte[] header = new byte[MIN_HEADER_SIZE + numberPageSegments];

        Utils.copyArrayToArray("OggS".getBytes(StandardCharsets.US_ASCII), header, 0);

        header[4] = (byte) version;

        BitSet flags = new BitSet();
        flags.set(0, continuation);
        flags.set(1, bos);
        flags.set(2, eos);

        byte[] flagsByte = flags.toByteArray();

        header[5] = flagsByte.length == 0 ? 0 : flagsByte[0];

        Utils.copyIntToArray(granulePosition, 8, header, 6);
        Utils.copyIntToArray(bitstreamSerialNumber, 4, header, 14);
        Utils.copyIntToArray(pageSequenceNumber, 4, header, 18);

        // Checksum @ 22 left blank for now

        header[26] = (byte) numberPageSegments;

        for(int i = 0; i < segmentTable.size(); i++){
            header[27 +i] = (byte) segmentTable.get(0).intValue();
        }

        return header;
    }

    private int generateChecksum() {

        CRC32 crc = new CRC32();

        crc.update(getHeader());

        for (Packet packet : packets) {
            byte[] packetArray = packet.toByteArray();
            crc.update(packetArray);
        }

        return (int) crc.getValue();
    }

    void setGeneratedChecksum() {

        this.checksum = generateChecksum();
    }

    private int getHeaderSize() {

        return MIN_HEADER_SIZE + numberPageSegments;
    }

    int getPageSize() {

        return getHeaderSize() + segmentTable.stream().mapToInt(Integer::intValue).sum();
    }

    byte[] toByteArray() {

        byte[] page = new byte[getPageSize()];

        int index = 0;

        Utils.copyArrayToArray(getHeader(), page, index);
        Utils.copyIntToArray(this.checksum, 4, page, 22);


        index += getHeaderSize();

        for (Packet packet : packets) {
            Utils.copyArrayToArray(packet.toByteArray(), page, index);
            index += page.length;
        }

        return page;
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder();

        str.append("-------OggPage-------\n");

        str.append("Signature: ").append(signature).append("\n")
                .append("Version: ").append(version).append("\n").
                append("Continuation: ").append(continuation).append("\n").
                append("Beginning of Stream: ").append(bos).append("\n").
                append("End of Stream: ").append(eos).append("\n").
                append("Granule Position: ").append(granulePosition).append("\n").
                append("Bitstream serial number: ").append(bitstreamSerialNumber).append("\n").
                append("Page Sequence Number: ").append(pageSequenceNumber).append("\n").
                append("CRC Checksum: ").append(checksum).append("\n").
                append("Number page segments: ").append(numberPageSegments).append("\n");

        for (int i = 0; i < numberPageSegments; i++) {
            str.append("Lacing Value #").append(i).append(": ").append(segmentTable.get(i)).append("\n");
        }

        packets.forEach(str::append);

        return str.toString();
    }
}

