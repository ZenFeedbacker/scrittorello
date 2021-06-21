package com.scritorrelo;

import java.io.ByteArrayInputStream;
import java.io.EOFException;

public class OggPage {

    byte[] data;
    ByteArrayInputStream stream;


    String signature;


    public OggPage(byte[] data) throws EOFException {
        this.data = data;
        this.stream = new ByteArrayInputStream(this.data);

        signature = Utils.readByteStreamToString(stream, 8);

    }
}
