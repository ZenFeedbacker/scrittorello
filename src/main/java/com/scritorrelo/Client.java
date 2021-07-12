package com.scritorrelo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@SpringBootApplication
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static final String sampleFile = "src/main/resources/speech.opus";
    public static final String outputFile = "src/main/resources/out.opus";

    public static ApplicationContext ctx;

    public WebSocketManager manager;

    public static void main(String[] args) throws Exception {

        SpringApplication.run(Client.class);
        Client client = new Client();
        client.init();
    }

    private void init() throws Exception {

        ctx = new AnnotationConfigApplicationContext(Client.class);
        manager = ctx.getBean(WebSocketManager.class);

        manager.init();

        System.out.println("connect");

        DatabaseManager.init();

        String line;

        while ((line = getInput().readLine()) != null) {
            if (line.equals("")) {
                break;
            }
        }
        manager.closeAll();

        System.out.println("Disconnect");
    }

    private static BufferedReader getInput() {
        return new BufferedReader(new InputStreamReader(System.in));
    }
}