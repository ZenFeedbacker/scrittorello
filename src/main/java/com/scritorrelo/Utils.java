package com.scritorrelo;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.math.BigInteger;
import java.util.BitSet;

public class Utils {

    public static byte[] readByteStream(ByteArrayInputStream stream, int len) throws EOFException {

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

    public static byte readByteStream(ByteArrayInputStream stream) throws EOFException {

        byte data;
        int ch;

        if ((ch = stream.read()) == -1) {
            throw new EOFException("End of stream.");
        }
        data = (byte) ch;


        return data;
    }

    public static int readByteStreamToInt(ByteArrayInputStream stream, int len) throws EOFException {


        return Integer.reverseBytes(new BigInteger(readByteStream(stream, len)).intValue());
    }

    public static int readByteStreamToInt(ByteArrayInputStream stream) throws EOFException {


        return Integer.reverseBytes(new BigInteger(readByteStream(stream, 1)).intValue());
    }

    public static int readByteStreamToIntBigEndian(ByteArrayInputStream stream) throws EOFException {


        return new BigInteger(readByteStream(stream, 1)).intValue();
    }

    public static String readByteStreamToString(ByteArrayInputStream stream, int len) throws EOFException {

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

    public static String bitSetToString(BitSet bi) {

        StringBuilder s = new StringBuilder();

        for (int i = 0; i < bi.length(); i++) {
            s.append(bi.get(i) ? 1 : 0);
        }

        return s.toString();
    }
}
