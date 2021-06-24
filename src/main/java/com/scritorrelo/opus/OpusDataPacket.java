package com.scritorrelo.opus;

import com.scritorrelo.Utils;

import java.io.EOFException;
import java.util.BitSet;

public class OpusDataPacket extends OpusPacket {

    int config;
    boolean stereo;
    int code;
    byte[] data;

    public OpusDataPacket(byte[] data) throws EOFException {
        super(data);


        BitSet tocBi = BitSet.valueOf(new byte[]{Utils.readByteStream(stream)});


        String tocStr = Utils.bitSetToString(tocBi);
        config = Integer.parseInt(tocStr.substring(0, 5), 2);
        stereo = tocBi.get(5);
        code = Integer.parseInt(tocStr.substring(6, 8), 2);

        data = Utils.readByteStream(stream, stream.available());
    }


    @Override
    public String toString() {

        return "Length: " + length + "\n" +
                "Config: " + config + "\n" +
                "Stereo: " + stereo + "\n" +
                "Code: " + code + "\n";
    }
}
