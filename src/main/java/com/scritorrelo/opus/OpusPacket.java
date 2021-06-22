package com.scritorrelo.opus;

import lombok.Getter;

import java.io.ByteArrayInputStream;

public abstract class OpusPacket {

    @Getter
    final
    byte[] data;
    final ByteArrayInputStream stream;

    public OpusPacket(byte[] data) {

        this.data = data;
        this.stream = new ByteArrayInputStream(this.data);

    }

    @Override
    public String toString() {


        return "Length: " + this.data.length + "\n";
    }
}
