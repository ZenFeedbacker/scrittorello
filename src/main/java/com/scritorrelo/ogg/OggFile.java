package com.scritorrelo.ogg;

import com.google.common.primitives.Bytes;
import com.scritorrelo.Utils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OggFile {

    byte[] file;

    public OggFile(String filename) throws IOException {

        file = FileUtils.readFileToByteArray(new File(filename));

        int index = 0;
        int subindex;

        List<Integer> indexes = new ArrayList<>();

        while (true) {
            subindex = Bytes.indexOf(Arrays.copyOfRange(file, index, file.length), Page.OGG_PAGE_HEADER.getBytes(StandardCharsets.US_ASCII));
            if (subindex == -1) {
                break;
            }
            indexes.add(index + subindex);
            index += subindex + 3;
        }


        indexes.add(file.length);

        List<byte[]> packets = new ArrayList<>();

        for (int i = 0; i < indexes.size() - 1; i++) {
            packets.add(Arrays.copyOfRange(file, indexes.get(i), indexes.get(i + 1)));
        }

        List<Page> pages = packets.stream().map(Page::new).collect(Collectors.toList());

        //pages.forEach(System.out::println);
    }

    public OggFile(Stream oggStream) {

        file = new byte[oggStream.pages.stream().mapToInt(Page::getPageSize).sum()];

        int idx = 0;

        for (Page page : oggStream.pages) {
            Utils.copyArraytoArray(page.toByteArray(), file, idx);
            idx += page.getPageSize();
        }
    }

    public void writeToFile(String filename) throws IOException {
        FileUtils.writeByteArrayToFile(new File(filename), file);

    }

}
