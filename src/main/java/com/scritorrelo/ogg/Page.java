package com.scritorrelo.ogg;

import com.scritorrelo.Utils;
import com.scritorrelo.opus.Packet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import static java.util.Objects.isNull;

@Builder
@AllArgsConstructor
public class Page {

    final static String OGG_PAGE_HEADER = "OggS";

    private final static int MIN_HEADER_SIZE = 27;

    byte[] data;
    final ByteArrayInputStream stream;

    String signature = OGG_PAGE_HEADER;
    int version = 0;
    boolean continuation = false;
    @Setter
    boolean EoS = false;
    @Setter
    boolean BoS = false;
    int granulePosition = 0;
    int bitstreamSerialNumber;
    int pageSequenceNumber = 0;
    long CRCChecksum;
    int numberPageSegments = 0;
    final List<Integer> segmentTable;
    List<Packet> packets;

    public Page(byte[] data) {

        this(new ByteArrayInputStream(data));
        this.data = data;
    }

    public Page(ByteArrayInputStream stream) {

        this.stream = stream;

        segmentTable = new ArrayList<>();
        packets = new ArrayList<>();

        try {
            signature = Utils.readByteStreamToString(stream, 4);
            version = Utils.readByteStreamToInt(stream);

            BitSet bits = BitSet.valueOf(new byte[]{Utils.readByteStream(stream)});
            continuation = bits.get(0);
            BoS = bits.get(1);
            EoS = bits.get(2);
            granulePosition = Utils.readByteStreamToInt(stream, 8);

            bitstreamSerialNumber = Utils.readByteStreamToInt(stream, 4);
            pageSequenceNumber = Utils.readByteStreamToInt(stream, 4);
            CRCChecksum = Utils.readByteStreamToInt(stream, 4);
            numberPageSegments = Utils.readByteStreamToIntBigEndian(stream);

            for (int i = 0; i < numberPageSegments; i++) {
                segmentTable.add(Utils.readByteToIntBigEndian(stream));
            }

            System.out.println(this);
            for (int segment : segmentTable) {
                if (segment != 0) {
                    Packet packet = Packet.PacketFactory(Utils.readByteStream(stream, segment));
                    packets.add(packet);
                    //System.out.println(packet);
                }
            }
        } catch (EOFException ignored) {
        }
    }

    public void addPacket(Packet packet) {

        packets.add(packet);
    }

    private byte[] getHeader() {
        byte[] header = new byte[MIN_HEADER_SIZE + numberPageSegments];

        Utils.copyArraytoArray("OggS".getBytes(StandardCharsets.US_ASCII), header, 0);

        header[4] = 0; // Version

        BitSet flags = new BitSet();
        flags.set(1, continuation);
        flags.set(2, BoS);
        flags.set(4, EoS);

        byte[] flagsByte = flags.toByteArray();

        header[5] = flagsByte.length == 0 ? 0 : flagsByte[0];

        Utils.copyIntToArray(granulePosition, 8, header, 6);
        Utils.copyIntToArray(bitstreamSerialNumber, 4, header, 14);
        Utils.copyIntToArray(pageSequenceNumber, 4, header, 18);

        // Checksum @ 22 left blank for now

        header[26] = (byte) numberPageSegments;

        // System.arraycopy(segmentTable, 0, header, MIN_HEADER_SIZE, numberPageSegments);

        return header;
    }

    public long generateChecksum() {

        Checksum checksum = new CRC32();

        byte[] header = getHeader();
        checksum.update(header, 0, header.length);

        for(Packet packet : packets){

        }
//        if (data != null && data.length > 0) {
//            checksum.update(data, header.length, data.length);
//        }

        return checksum.getValue();
    }


    public int getHeaderSize() {

        return MIN_HEADER_SIZE + numberPageSegments;
    }

    public int getPageSize() {

        return getHeaderSize() + segmentTable.stream().mapToInt(Integer::intValue).sum();
    }

    public byte[] toByteArray() {

        byte[] page = new byte[getPageSize()];

        Utils.copyArraytoArray(getHeader(), page, 0);
        if (!isNull(data) && data.length > 0) {
            Utils.copyArraytoArray(data, page, getHeaderSize());
        }
        return page;
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder();

        str.append("-------OggPage-------\n");

        if (!isNull(data)) {
            str.append("Length: ").append(data.length).append("\n");
        }

        str.append("Signature: ").append(signature).append("\n").
                append("Version: ").append(version).append("\n").
                append("Is Continuation: ").append(continuation).append("\n").
                append("Beginning of Stream: ").append(BoS).append("\n").
                append("End of Stream: ").append(EoS).append("\n").
                append("Granule Position: ").append(granulePosition).append("\n").
                append("Bitstream serial number: ").append(bitstreamSerialNumber).append("\n").
                append("Page Sequence Number: ").append(pageSequenceNumber).append("\n").
                append("CRC Checksum: ").append(CRCChecksum).append("\n").
                append("Number page segments: ").append(numberPageSegments).append("\n");

        for (int i = 0; i < numberPageSegments; i++) {
            str.append("Lacing Value #").append(i).append(": ").append(segmentTable.get(i)).append("\n");
        }

        return str.toString();
    }
}
