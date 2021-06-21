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

    @Override
    public String toString() {


        return "Length: " + this.data.length + "\n";
    }
}
