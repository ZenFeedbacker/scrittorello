package com.scritorrelo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Scrittorello {

    public static void main(String[] args) {
        SpringApplication.run(Scrittorello.class);
    }
}

