package com.scritorrelo.zello.message.image;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Slf4j
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

    public void save(String pathname)  {

        var photo = new File(pathname);

        if (photo.exists()){
            try {
                Files.delete(Paths.get(pathname));
                log.info("File " + pathname + " already existed, was deleted.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileOutputStream fos = new FileOutputStream(photo.getPath())) {
            fos.write(data);
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
    }
}
