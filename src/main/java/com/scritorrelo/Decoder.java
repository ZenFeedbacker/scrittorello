package com.scritorrelo;

import com.scritorrelo.zello.message.audio.Audio;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.gagravarr.ogg.CRCUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

@Slf4j
public class Decoder {

    private static final String OPUS_ID_HEADER = "OpusHead";
    private static final String OPUS_COMMENT_HEADER = "OpusTags";
    private static final String OGG_PAGE_HEADER = "OggS";

    private final String path;

    private final Deque<byte[]> packets = new ArrayDeque<>();
    private int pageIndex = 0;
    private final int bitstreamSerialNumber = Utils.randomStreamSerialNumber();

    public Decoder(Audio audio) {

        audio.getAudioFrames().forEach(f -> packets.add(f.getData()));

        path = audio.getOggPath();
    }

    public void writeToFile() {

        try {
            FileUtils.writeByteArrayToFile(new File(path), getOGG());
        } catch (IOException e) {
            log.warn("IOException while writing ogg file to {}: {}", path, e.getMessage());
        }
    }

    private byte[] getOGG() {

        var oggData = getPage(getIDHeader(), 2);
        oggData = ArrayUtils.addAll(oggData, getPage(getCommentHeader(), 0));

        while (!this.packets.isEmpty()) {
            oggData = ArrayUtils.addAll(oggData, getPage(packets.remove(), packets.isEmpty() ? 4 : 0));
        }

        return oggData;
    }

    private byte[] getPage(byte[] segmentData, int headerType) {

        var bb = ByteBuffer.allocate(28 + segmentData.length);

        // Page Header
        bb.put(OGG_PAGE_HEADER.getBytes(StandardCharsets.UTF_8));
        // Version
        bb.put((byte) 0);
        // Header type
        bb.put((byte) headerType);
        // Granule position
        bb.putLong(new BigInteger("ffffffff", 16).longValue());
        // Bitstream serial number
        bb.put(Utils.intToLittleEndianByteArray(bitstreamSerialNumber, 4));
        // Page sequence number
        bb.put(Utils.intToLittleEndianByteArray(pageIndex++, 4));
        // CRC checksum temporary
        bb.putInt(0);
        // Page segments
        bb.put((byte) 1);
        // Segments table
        bb.put((byte) segmentData.length);

        int checksum = CRCUtils.getCRC(bb.array());

        // Segment data
        bb.put(segmentData);

        if (segmentData.length > 0) {
            checksum = CRCUtils.getCRC(segmentData, checksum);
        }

        // CRC checksum
        byte[] page = bb.array();

        Utils.copyArrayToArray(Utils.intToLittleEndianByteArray(checksum, 4), page, 22);

        return page;
    }

    private byte[] getIDHeader() {

        var bb = ByteBuffer.allocate(19);

        // ID package header
        bb.put(OPUS_ID_HEADER.getBytes(StandardCharsets.UTF_8));
        // Version
        bb.put((byte) 1);
        // Channel count
        bb.put((byte) 1);
        // Pre-skip
        bb.putShort((short) 0);
        // Sample rate
        bb.put(Utils.intToLittleEndianByteArray(16000, 4));
        // Output gain
        bb.putShort((short) 0);
        // Channel map
        bb.put((byte) 0);

        return bb.array();
    }

    private byte[] getCommentHeader() {

        var bb = ByteBuffer.allocate(20);

        bb.put(OPUS_COMMENT_HEADER.getBytes(StandardCharsets.UTF_8));

        // Vendor string length
        bb.put(Utils.intToLittleEndianByteArray(4, 4));

        // Vendor string
        bb.put("abcd".getBytes(StandardCharsets.UTF_8));

        // User comment List length
        bb.putInt(0);

        return bb.array();
    }
}
