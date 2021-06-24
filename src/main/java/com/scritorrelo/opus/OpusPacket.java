package com.scritorrelo.opus;

import java.io.ByteArrayInputStream;

public abstract class OpusPacket {

    final ByteArrayInputStream stream;
    int length;

    public OpusPacket(byte[] data) {

        this.length = data.length;
        this.stream = new ByteArrayInputStream(data);
    }

    @Override
    public String toString() {

        return "Length: " + this.length + "\n";
    }
}
