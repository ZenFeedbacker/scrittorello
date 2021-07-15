package com.scritorrelo.ogg;

import com.google.common.primitives.Bytes;
import com.scritorrelo.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class OggFile {

    byte[] file;
    List<OggPage> pages;

    public OggFile(String filename) throws IOException {

        file = FileUtils.readFileToByteArray(new File(filename));

        int index = 0;
        int subindex;

        List<Integer> indexes = new ArrayList<>();

        while (true) {
            subindex = Bytes.indexOf(Arrays.copyOfRange(file, index, file.length), OggPage.OGG_PAGE_HEADER.getBytes(StandardCharsets.US_ASCII));
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

        log.info("Number of pages: " + packets.size());

        pages = packets.stream().map(OggPage::new).collect(Collectors.toList());

        pages.forEach(p -> log.info(p.toString()));
    }

    public OggFile(OggStream oggStream) {

        file = new byte[oggStream.oggPages.stream().mapToInt(OggPage::getPageSize).sum()];

        int idx = 0;

        for (OggPage page : oggStream.oggPages) {
            Utils.copyArrayToArray(page.toByteArray(), file, idx);
            idx += page.getPageSize();
        }
    }

    public void writeToFile(String filename) throws IOException {

        FileUtils.writeByteArrayToFile(new File(filename), file);
    }

}
