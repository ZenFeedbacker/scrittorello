package com.scritorrelo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    private static final String EOS_EXCEPTION = "End of Stream.";

    public static byte[] readByteStream(ByteArrayInputStream stream, int len) throws EOFException {

        byte[] data = new byte[len];
        int ch;

        for (int i = 0; i < len; i++) {
            if ((ch = stream.read()) == -1) {
                throw new EOFException(EOS_EXCEPTION);
            }
            data[i] = (byte) ch;
        }

        return data;
    }

    public static byte[] readRemainingByteStream(ByteArrayInputStream stream) throws EOFException {

        return readByteStream(stream, stream.available());
    }

    public static byte readByteStream(ByteArrayInputStream stream) throws EOFException {

        byte data;
        int ch;

        if ((ch = stream.read()) == -1) {
            throw new EOFException(EOS_EXCEPTION);
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


        return new BigInteger(readByteStream(stream, 1) ).intValue();
    }

    public static int readByteToIntBigEndian(ByteArrayInputStream stream) throws EOFException {


        return readByteStream(stream) & 0xFF;
    }

    public static String readByteStreamToString(ByteArrayInputStream stream, int len) throws EOFException {

        char[] data = new char[len];
        int ch;

        for (int i = 0; i < len; i++) {
            if ((ch = stream.read()) == -1) {
                throw new EOFException(EOS_EXCEPTION);
            }
            data[i] = (char) ch;
        }

        return new String(data);
    }

    public static String readByteArrayToString(byte[] stream, int len) {

        return new String(Arrays.copyOf(stream, len));
    }

    public static void copyArrayToArray(byte[] from, byte [] to, int pos){
        System.arraycopy(from, 0, to,  pos, from.length);
    }

    public static void copyIntToArray(int from, int len, byte [] to, int pos){
        ByteBuffer byteBuffer = ByteBuffer.allocate(len);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(from);
        byte[] fromArray = byteBuffer.array();
        System.arraycopy(fromArray, 0, to,  pos, fromArray.length);
    }

    public static int randomStreamSerialNumber(){

        return  ThreadLocalRandom.current().nextInt();
    }
}
