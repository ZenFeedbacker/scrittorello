package com.scritorrelo;

import java.io.EOFException;
import java.util.BitSet;

public class OggDataPacket extends OggPacket {

    int config;
    boolean stereo;
    int code;

    public OggDataPacket(byte[] data) throws EOFException {

        super(data);

        BitSet tocBi = BitSet.valueOf(new byte[]{Utils.readByteStream(stream)});


        String tocStr = bitSetToString(tocBi);
        config = Integer.parseInt(tocStr.substring(0,5), 2);
        stereo = tocBi.get(5);
        code  = Integer.parseInt(tocStr.substring(6,8),2);
    }

     public String bitSetToString(BitSet bi) {

        StringBuilder s = new StringBuilder();

        for (int i = 0; i < bi.length(); i++) {
            s.append(bi.get(i) ? 1 : 0);
        }

        return s.toString();
    }

    @Override
    public String toString() {

        return "Length: " + data.length + "\n" +
                "Config: " + config + "\n" +
                "Stereo: " + stereo + "\n" +
                "Code: " + code + "\n";
    }
}
