package com.scritorrelo.ogg;

import com.google.common.primitives.Bytes;
import com.scritorrelo.Utils;
import lombok.Getter;
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

    private byte[] file;
    @Getter
    private List<OggPage> pages;

    public OggFile(String filename) {

        try {
            file = FileUtils.readFileToByteArray(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

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

        file = new byte[oggStream.getOggPages().stream().mapToInt(OggPage::getPageSize).sum()];

        int idx = 0;

        for (OggPage page : oggStream.getOggPages()) {
            Utils.copyArrayToArray(page.toByteArray(), file, idx);
            idx += page.getPageSize();
        }
    }

    public void writeToFile(String filename)  {

        try {
            FileUtils.writeByteArrayToFile(new File(filename), file);
        } catch (IOException e) {
            log.warn("IOException while writing ogg file to {}: {}", filename, e.getMessage());
        }
    }

}
