package com.scritorrelo;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.math.BigInteger;

public class OggPacket {

    byte[] data;
    ByteArrayInputStream stream;


    public OggPacket(byte[] data) throws EOFException {

        this.data = data;
        System.out.println(data.length);
    }

    private byte[] readByteStream(int len) throws EOFException {

        byte[] data = new byte[len];
        int ch;

        for (int i = 0; i < len; i++) {
            if ((ch = stream.read()) == -1) {
                throw new EOFException("End of stream.");
            }
            data[i] = (byte) ch;
        }

        return data;
    }

    byte readByteStream() throws EOFException {

        byte data;
        int ch;

        if ((ch = stream.read()) == -1) {
            throw new EOFException("End of stream.");
        }
        data = (byte) ch;


        return data;
    }

    int readByteStreamToInt(int len) throws EOFException {


        return Integer.reverseBytes(new BigInteger(readByteStream(len)).intValue());
    }

    int readByteStreamToInt() throws EOFException {


        return Integer.reverseBytes(new BigInteger(readByteStream(1)).intValue());
    }

    String readByteStreamToString(int len) throws EOFException {

        char[] data = new char[len];
        int ch;

        for (int i = 0; i < len; i++) {
            if ((ch = stream.read()) == -1) {
                throw new EOFException("End of stream.");
            }
            data[i] = (char) ch;
        }

        return new String(data);
    }

/*
        # The Ogg page has the following format:
        #  0               1               2               3                Byte
        #  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1| Bit
        # +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        # | capture_pattern: Magic number for page start "OggS"           | 0-3
        # +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        # | version       | header_type   | granule_position              | 4-7
        # +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        # |                                                               | 8-11
        # +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        # |                               | bitstream_serial_number       | 12-15
        # +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        # |                               | page_sequence_number          | 16-19
        # +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        # |                               | CRC_checksum                  | 20-23
        # +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        # |                               | page_segments | segment_table | 24-27
        # +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        # | ...                                                           | 28-
        # +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
*/

    @Override
    public String toString() {

        String str = "Length: " + this.data.length + "\n";


        return str;
    }
}
