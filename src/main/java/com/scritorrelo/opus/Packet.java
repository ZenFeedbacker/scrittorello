package com.scritorrelo.opus;

import java.io.ByteArrayInputStream;

public abstract class Packet {

    final ByteArrayInputStream stream;
    final int length;

    public Packet(byte[] data) {

        this.length = data.length;
        this.stream = new ByteArrayInputStream(data);
    }

    @Override
    public String toString() {

        return "Length: " + this.length + "\n";
    }
}
