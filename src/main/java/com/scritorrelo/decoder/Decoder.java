package com.scritorrelo.decoder;

import com.scritorrelo.Utils;
import com.scritorrelo.opus.packet.Packet;
import com.scritorrelo.zello.message.audio.Audio;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.zip.CRC32;

@Slf4j
public class Decoder {

    private static final String OPUS_ID_HEADER = "OpusHead";
    private static final String OPUS_COMMENT_HEADER = "OpusTags";
    private static final String OGG_PAGE_HEADER = "OggS";

    private final String path;


    private final Deque<byte[]> packets = new ArrayDeque<>();
    private int pageIndex = 0;
    private final int bitstreamSerialNumber = Utils.randomStreamSerialNumber();
    private final ByteArrayOutputStream oggData = new ByteArrayOutputStream();
    private int[] checksumTable;
    private byte[] oggHeader;


    public Decoder(Audio audio) {

        audio.getAudioFrames().forEach(f -> packets.add(f.getData()));

        path = audio.getOggPath();

        initChecksumTable();

        this.oggHeader = getPage(getIDHeader(), 2);
        this.oggHeader = ArrayUtils.addAll(this.oggHeader, this.getPage(getCommentHeader(), 0));
    }

    public void writeToFile() {

        try {
            FileUtils.writeByteArrayToFile(new File(path), getOGG());
        } catch (IOException e) {
            log.warn("IOException while writing ogg file to {}: {}", path, e.getMessage());
        }
    }

    private byte[] getOGG() {

        byte[] data = this.oggHeader;

        while (!this.packets.isEmpty()) {
            byte[] packet = this.packets.remove();
            data = ArrayUtils.addAll(oggData.toByteArray(), this.getPage(packet, this.packets.isEmpty() ? 4 : 0));
        }

        this.pageIndex = 2;

        return data;
    }

    private byte[] getPage(byte[] segmentData, int headerType) {

        var bb = ByteBuffer.allocate(28 + segmentData.length);

        bb.put(OGG_PAGE_HEADER.getBytes(StandardCharsets.UTF_8));

        System.out.println("Version position: " + bb.position());
        bb.put((byte)0);

        System.out.println("Header type  position: " + bb.position());
        bb.put((byte)headerType);

        System.out.println("Granule position: " + bb.position());
        bb.putLong(new BigInteger("ffffffff", 16).longValue());

        System.out.println("Bitstream serial number: " + bb.position());
        bb.putInt(bitstreamSerialNumber);

        System.out.println("Page sequence number: " + bb.position());
        bb.putInt(pageIndex++);

        System.out.println("CRC: " +bb.position());
        bb.putInt(0);

        System.out.println("Page segments:" + bb.position());
        bb.put((byte)1);

        System.out.println("Segments table: " + bb.position());
        bb.put((byte) segmentData.length);

        System.out.println("Segment data: " + bb.position());
        bb.put(segmentData);

        byte[] page = bb.array();
        Utils.copyIntToArray(generateChecksum(segmentData), 4, page, 22);

        return page;
    }

    private byte[] getIDHeader() {

        var bb = ByteBuffer.allocate(19);

        bb.put(OPUS_ID_HEADER.getBytes(StandardCharsets.UTF_8));
        bb.put((byte) 1);
        bb.put((byte) 1);
        bb.putShort((short) 0);
        bb.putInt(16000);
        bb.putShort((short) 0);
        bb.put((byte) 0);

        return bb.array();
    }

    private byte[] getCommentHeader() {

        var bb = ByteBuffer.allocate(20);

        bb.put(OPUS_COMMENT_HEADER.getBytes(StandardCharsets.UTF_8));
        bb.putInt(4);
        bb.put("abcd".getBytes(StandardCharsets.UTF_8));
        bb.putInt(0);

        return bb.array();
    }

    private int getChecksum(byte[] data) {
        int checksum = 0;
        for (byte datum : data) {
            checksum = (checksum << 8) ^ checksumTable[((checksum >>> 24) & 0xff) ^ datum];
        }
        return checksum >>> 0;
    }

    private int generateChecksum(byte[] data) {

        CRC32 crc = new CRC32();
        crc.update(data);

        return (int) crc.getValue();
    }

    private void initChecksumTable() {
        checksumTable = new int[256];
        for (var i = 0; i < 256; i++) {
            var r = i << 24;
            for (var j = 0; j < 8; j++) {
                r = ((r & 0x80000000) != 0) ? ((r << 1) ^ 0x04c11db7) : (r << 1);
            }
            checksumTable[i] = (r);
        }
    }
}
