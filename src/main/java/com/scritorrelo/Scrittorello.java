package com.scritorrelo;

import com.scritorrelo.ogg.OggFile;
import com.scritorrelo.ogg.OggPage;
import de.jarnbjo.ogg.FileStream;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.RandomAccessFile;

@SpringBootApplication
public class Scrittorello {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Scrittorello.class);

//        String filepath = "src/main/resources/speech.opus";
//        OggFile oggFile = new OggFile(filepath);
//
//        OggPage page = oggFile.getPages().get(0);
//        System.out.println(page);
//        FileStream fileStream = new FileStream(new RandomAccessFile(filepath, "r"));
//        de.jarnbjo.ogg.OggPage oggPage = fileStream.getOggPage(0);
//        System.out.println(ToStringBuilder.reflectionToString(oggPage));
    }
}

