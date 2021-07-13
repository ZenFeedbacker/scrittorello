package com.scritorrelo.zello.message.image;

import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.Arrays;

@ToString
public class ImagePacket {

    private final byte packetType;
    @Getter
    private final int id;
    @Getter
    private final boolean isThumbnail;
    private final byte[] data;

    public ImagePacket(byte[] binary) {

        packetType = binary[0];
        id = new BigInteger(Arrays.copyOfRange(binary, 1, 5)).intValue();
        isThumbnail = new BigInteger(Arrays.copyOfRange(binary, 5, 9)).intValue() == 2;
        data = Arrays.copyOfRange(binary, 9, binary.length);
    }

    public void save() {
        File photo = new File("photo.jpg");

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
