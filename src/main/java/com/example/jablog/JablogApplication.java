package com.example.jablog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan("com.example.jablog.entity")
@EnableScheduling
public class JablogApplication {

    public static void main(String[] args) {
        SpringApplication.run(JablogApplication.class, args);
    }

}
