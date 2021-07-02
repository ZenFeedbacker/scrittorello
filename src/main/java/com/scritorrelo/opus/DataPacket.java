package com.scritorrelo.opus;

import com.scritorrelo.Utils;
import lombok.Getter;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;

import java.io.EOFException;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

public class DataPacket extends Packet {

    int config;
    boolean stereo;
    int code;
    @Getter
    byte[] data;

    public DataPacket(byte[] data) {

        super(data);

        try {
            byte toc = Utils.readByteStream(stream);
            String tocStr = String.format("%8s", Integer.toBinaryString(toc & 0xFF)).replace(' ', '0');

            config = Integer.parseInt(tocStr.substring(0, 5), 2);
            stereo = tocStr.charAt(5) == '1';
            code = Integer.parseInt(tocStr.substring(6, 8), 2);

            this.data = Utils.readByteStream(stream, stream.available());
        } catch (EOFException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] toByteArray() {


        return isNotEmpty(packet) ? packet : new byte[0];
    }

    private char[] tocToCharArr() {
        char[] config = String.format("%5s", Integer.toBinaryString(this.config & 0xFF)).replace(' ', '0').toCharArray();
        char[] stereo = new char[1];
        stereo[0] = this.stereo ? '1' : '0';
        char[] code = String.format("%2s", Integer.toBinaryString(this.code & 0xFF)).replace(' ', '0').toCharArray();

        return ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(config, stereo), code));
    }

    @Override
    public String toString() {

        return "-------Opus Data Packet-------\n" +
                "Length: " + length + "\n" +
                "Config: " + config + "\n" +
                "Stereo: " + stereo + "\n" +
                "Code: " + code + "\n" +
                "Data: " + new String(Hex.encodeHex(data)) + "\n";
    }
}
