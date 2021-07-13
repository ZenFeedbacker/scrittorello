package com.scritorrelo.zello.message.image;

import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.Arrays;

@ToString
@Getter
public class ImagePacket {

    private final byte packetType;
    private final int id;
    private final boolean isThumbnail;
    private final byte[] data;

    public ImagePacket(byte[] binary) {

        packetType = binary[0];
        id = new BigInteger(Arrays.copyOfRange(binary, 1, 5)).intValue();
        isThumbnail = new BigInteger(Arrays.copyOfRange(binary, 5, 9)).intValue() == 2;
        data = Arrays.copyOfRange(binary, 9, binary.length);
    }

    public void save(String pathname) {
        File photo = new File(pathname);

        if (photo.exists()) {
            photo.delete();
        }

        try {
            FileOutputStream fos = new FileOutputStream(photo.getPath());
            fos.write(data);
            fos.close();
        } catch (java.io.IOException e) {
            System.out.println("Error " + e);
        }
    }
}
