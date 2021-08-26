package com.scritorrelo;

import com.scritorrelo.ogg.OggFile;
import org.gagravarr.ogg.tools.OggInfoTool;
import org.gagravarr.opus.tools.OpusInfoTool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class Scrittorello {

    public static void main(String[] args) {
        SpringApplication.run(Scrittorello.class);


        try {
            String path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "sample1.opus";
            OggFile sampleFile = new OggFile(path);
            File oggFile = new File(path);
            OpusInfoTool opusInfo = new OpusInfoTool();
            System.out.println("\n-----OPUS INFO:");
            opusInfo.process(oggFile, true);
            OggInfoTool oggInfoTool = new OggInfoTool(oggFile);
            System.out.println("\n-----OGG INFO:");
            oggInfoTool.printStreamInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

