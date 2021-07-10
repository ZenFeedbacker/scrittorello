package com.scritorrelo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@SpringBootApplication
@EnableJpaRepositories(basePackages={"com.scritorrelo.repository"})
@EntityScan(basePackages={"com.scritorrelo"})
@ComponentScan(basePackages={"com.scritorrelo"})
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static final String sampleFile = "src/main/resources/speech.opus";
    public static final String outputFile = "src/main/resources/out.opus";

    public static ApplicationContext ctx;

    @Autowired
    public static WebSocketManager manager;

    public static void main(String[] args) throws Exception {

        SpringApplication.run(Client.class);
        ctx = new AnnotationConfigApplicationContext(Client.class);
        manager = ctx.getBean(WebSocketManager.class);

        manager.init();

        System.out.println("connect");

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