package com.scritorrelo;

import com.scritorrelo.ogg.OggFile;
import de.jarnbjo.ogg.FileStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.RandomAccessFile;

@SpringBootApplication
public class Scrittorello {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Scrittorello.class);

        String filepath = "src/main/resources/speech.opus";
        OggFile oggFile = new OggFile(filepath);
        //oggFile.getPages().forEach(OggPage::checkChecksum);

        //oggFile.getPages().get(0).checkChecksum();
        FileStream fileStream = new FileStream(new RandomAccessFile(filepath, "r"));
        //de.jarnbjo.ogg.OggPage oggPage = fileStream.getOggPage(0);
        //System.out.println(oggPage.getPageCheckSum());

        for(int i  = 0; i < oggFile.getPages().size(); i++){
            oggFile.getPages().get(i).checkChecksum();
            //System.out.println(fileStream.getOggPage(i).getPageCheckSum());
        }
    }
}

