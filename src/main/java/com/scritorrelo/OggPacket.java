package com.scritorrelo;

import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.math.BigInteger;

public abstract class OggPacket {

    @Getter
    byte[] data;
    ByteArrayInputStream stream;

    public OggPacket(byte[] data) {

        this.data = data;
        this.stream = new ByteArrayInputStream(this.data);

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

    @Override
    public String toString() {


        return "Length: " + this.data.length + "\n";
    }
}
