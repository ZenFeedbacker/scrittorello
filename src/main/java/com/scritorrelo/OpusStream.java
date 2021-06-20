package com.scritorrelo;

import com.google.common.primitives.Bytes;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.naming.NameNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Objects.isNull;


public class OpusStream {

    private static final String OPUS_HEADER = "OpusHead";
    private static final String OPUS_TAGS_HEADER = "OpusTags";
    private static final String OGG_HEADER = "OggS";


    ByteArrayInputStream opusFile;
    byte[] segment_sizes;
    int segment_idx;
    int segments_count;
    int sequence_number;
    int opus_headers_count;
    int sample_rate;
    double packet_duration;
    int frames_per_packet;
    byte[][] saved_packets;


    public OpusStream(String filename) throws NameNotFoundException, IOException {

        this.opusFile = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(filename)));

        this.segment_sizes = new byte[0];
        this.saved_packets = new byte[0][0];

        this.segment_idx = 0;
        this.segments_count = 0;
        this.sequence_number = -1;
        this.opus_headers_count = 0;
        this.packet_duration = 0;
        this.frames_per_packet = 0;

        this.fill_opus_config();
    }

    public byte[] get_next_opus_packet() {
        boolean continue_needed = false;
        byte[] data = new byte[0];

        if (this.all_headers_parsed() && this.saved_packets.length > 0) {
            data = this.saved_packets[0];
            this.saved_packets = Arrays.copyOfRange(this.saved_packets, 1, this.saved_packets.length);
            return data;
        }

        while (true) {
            if (this.segment_idx >= this.segments_count) {
                int last_seq_num = this.sequence_number;

                if (this.get_next_ogg_packet_start()) {
                    this.parse_ogg_packet_header();
                } else {
                    return null;
                }

                if (continue_needed && (last_seq_num + 1) != this.sequence_number) {
                    this.segments_count = -1;
                    System.out.println("Skipping frame: continuation sequence is broken");
                    continue;
                }
            }

            List<Object> segment_data = this.get_ogg_segment_data();
            continue_needed = (boolean) segment_data.get(1);
            data = ArrayUtils.addAll(data, (byte[]) segment_data.get(0));

            if (continue_needed) {
                    continue;
            }

            if (!this.all_headers_parsed()) {
                this.parse_opus_headers(data);
                data = new byte[0];
                continue;
            }

            List<Double> opus_toc = parse_opus_toc(data);

            if (this.frames_per_packet != opus_toc.get(0) || this.packet_duration != opus_toc.get(1)) {
                data = new byte[0];
                System.out.println("Skipping frame - TOC differs");
                continue;
            }

            return data;
        }

        //return null;
    }

    private void fill_opus_config() throws NameNotFoundException {

        while (!this.all_headers_parsed()) {
            byte[] packet = this.get_next_opus_packet();
            if (isNull(packet)) {
                throw new NameNotFoundException("Invalid Opus file");
            }
        }
    }

    private List<Object> get_ogg_segment_data() {
        byte[] data = new byte[0];
        boolean continue_needed = false;

        while (this.segment_idx < this.segments_count) {
            int segment_size = this.segment_sizes[this.segment_idx];
            byte[] segment = new byte[0];
            this.opusFile.read(segment, 0, segment_size);
            data = ArrayUtils.addAll(data, segment);
            continue_needed = (segment_size == 255);
            this.segment_idx += 1;
            if (!continue_needed) {
                break;
            }
        }

        return Arrays.asList(data, continue_needed);
    }

    private void parse_opus_headers(byte[] data) {
        if (this.opus_headers_count < 1) {
            if (this.parse_opushead_header(data)) {
                this.opus_headers_count += 1;
            }
        } else if (this.opus_headers_count < 2) {
            if (parse_opustags_header(data)) {
                this.opus_headers_count += 1;
            }
        } else if (this.opus_headers_count < 3) {
            List<Double> opus_toc = parse_opus_toc(data);
            this.frames_per_packet = opus_toc.get(0).intValue();
            this.packet_duration = opus_toc.get(1);
            this.saved_packets = addArray(this.saved_packets, data);
            this.opus_headers_count += 1;
        }
    }

    private void parse_ogg_packet_header() {

        int version = this.opusFile.read();
        int header_type = this.opusFile.read();

        byte[] granule = new byte[0];
        this.opusFile.read(granule, 0, 8);

        byte[] serial_num = new byte[0];
        this.opusFile.read(serial_num, 0, 4);

        byte[] temp = new byte[0];
        this.opusFile.read(temp, 0, 4);
        this.sequence_number = new BigInteger(temp).intValue();

        this.opusFile.read(temp, 0, 4);
        int checksum = new BigInteger(temp).intValue();

        this.opusFile.read(temp, 0, 4);
        this.segments_count = new BigInteger(temp).intValue();

        this.segment_idx = 0;

        if (this.segments_count > 0) {
            this.opusFile.read(this.segment_sizes, 0, this.segments_count);
        }
    }

    private boolean get_next_ogg_packet_start() {

        byte[] magic = "OggS".getBytes(StandardCharsets.US_ASCII);
        int verified_bytes = 0;

        while (true) {
            byte[] packet_start = new byte[1];
            this.opusFile.read(packet_start, 0, 1);
            if (!Arrays.equals(packet_start, new byte[packet_start.length])) {
                return false;
            }

            if (packet_start[0] == magic[verified_bytes]) {
                verified_bytes += 1;
                if (verified_bytes == 4) {
                    return true;
                }
            } else {
                verified_bytes = 0;
            }
        }
    }

    private boolean parse_opushead_header(byte[] data) {

        if (Bytes.indexOf(data, "OpusHead".getBytes(StandardCharsets.US_ASCII)) != -1) {
            return false;
        }

        byte version = data[8];
        byte channels = data[9];
        int preskip = java.nio.ByteBuffer.wrap(Arrays.copyOfRange(data, 10, 12)).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
        this.sample_rate = java.nio.ByteBuffer.wrap(Arrays.copyOfRange(data, 12, 15)).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();

        return true;
    }

    private boolean all_headers_parsed() {

        return this.opus_headers_count >= 3;
    }

    private static List<Double> parse_opus_toc(byte[] data) {

        double frames_per_packet;
        int toc_c = data[0] & 0x03;

        if (toc_c == 0) {
            frames_per_packet = 1;
        } else if (toc_c == 1 || toc_c == 2) {
            frames_per_packet = 2;
        } else {
            System.out.println("An arbitrary number of frames in the packet - possible audio artifacts");
            frames_per_packet = 1;
        }

        HashMap<Double, List<Integer>> configs_ms = new HashMap<>();
        configs_ms.put(2.5, Arrays.asList(16, 20, 24, 28));
        configs_ms.put(5.0, Arrays.asList(17, 21, 25, 29));
        configs_ms.put(10.0, Arrays.asList(0, 4, 8, 12, 14, 18, 22, 26, 30));
        configs_ms.put(20.0, Arrays.asList(1, 5, 9, 13, 15, 19, 23, 27, 31));
        configs_ms.put(40.0, Arrays.asList(2, 6, 10));
        configs_ms.put(60.0, Arrays.asList(3, 7, 11));

        double[] durations = new double[]{2.5, 5, 10, 20, 40, 60};
        double conf = data[0] >> 3 & 0x1f;

        for (double duration : durations)
            if (configs_ms.get(duration).contains(conf)) {
                return Arrays.asList(frames_per_packet, duration);
            }

        return Arrays.asList(frames_per_packet, 20.0);
    }

    private static boolean parse_opustags_header(byte[] data) {

        return Bytes.indexOf(data, "OpusTags".getBytes(StandardCharsets.US_ASCII)) == -1;

    }

    private static byte[][] addArray(byte[][] array2d, byte[] array1d) {

        byte[][] newArray = Arrays.copyOf(array2d, array2d.length + 1);
        newArray[newArray.length - 1] = array1d;

        return newArray;
    }
}
