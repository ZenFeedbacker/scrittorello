package com.scritorrelo.ogg;

import com.google.common.primitives.Bytes;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class OggFile {

    @Getter
    private List<OggPage> pages;

    public OggFile(String filename) {

        byte[] file;
        try {
            file = FileUtils.readFileToByteArray(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        var index = 0;
        int subindex;

        var indexes = new ArrayList<Integer>();

        while (true) {
            subindex = Bytes.indexOf(Arrays.copyOfRange(file, index, file.length), OggPage.OGG_PAGE_HEADER.getBytes(StandardCharsets.US_ASCII));
            if (subindex == -1) {
                break;
            }
            indexes.add(index + subindex);
            index += subindex + 3;
        }


        indexes.add(file.length);

        var packets = new ArrayList<byte[]>();

        for (int i = 0; i < indexes.size() - 1; i++) {
            packets.add(Arrays.copyOfRange(file, indexes.get(i), indexes.get(i + 1)));
        }

        System.out.println("Number of pages: " + packets.size());

        pages = packets.stream().map(OggPage::new).collect(Collectors.toList());

        pages.forEach(p -> System.out.println(p.toString()));
    }
}
