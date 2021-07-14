package com.scritorrelo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
public class Client implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static final String sampleFile = "src/main/resources/speech.opus";
    public static final String outputFile = "src/main/resources/out.opus";

    @Autowired
    private DatabaseManager dbManager;
    @Autowired
    private WebSocketManager wsManager;

    public static void main(String[] args) {
        SpringApplication.run(Client.class);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        wsManager.init();

        System.out.println("connect");

        dbManager.init();

        String line;

        while ((line = getLine()) != null) {
            if (line.equals("")) {
                break;
            }
        }
        wsManager.closeAll();

        System.out.println("Disconnect");
    }

    private static String getLine() throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        return bf.readLine();
    }

}