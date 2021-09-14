package com.scritorrelo;

import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileReader;
import java.io.IOException;

@Slf4j
@SpringBootApplication
public class Scrittorello {


    public static void main(String[] args) {
        SpringApplication.run(Scrittorello.class);
        printInfo();
    }

    static void printInfo(){

        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            log.info("Running version {}", model.getVersion());
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }
}

