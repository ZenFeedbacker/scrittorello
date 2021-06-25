package com.scritorrelo.ogg;

import com.google.common.primitives.Bytes;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OggFile {

    byte[] file;

    public OggFile(String filename) throws IOException {
        file = FileUtils.readFileToByteArray(new File(filename));

        int index = 0;
        int subindex;
        List<Integer> indexes = new ArrayList<>();

        while (true) {
            subindex = Bytes.indexOf(Arrays.copyOfRange(file, index, file.length), "OggS".getBytes(StandardCharsets.US_ASCII));
            if (subindex == -1) {
                break;
            }
            indexes.add(index + subindex);
            index += subindex + 3;
        }

        indexes.add(file.length);

        List<byte[]> packets = new ArrayList<>();

        for (int i = 0; i < indexes.size() - 1; i++) {
            packets.add(Arrays.copyOfRange(file, indexes.get(0), indexes.get(i + 1)));
        }

        Page first = new Page(packets.get(0));
        System.out.println(first);
    }
}
