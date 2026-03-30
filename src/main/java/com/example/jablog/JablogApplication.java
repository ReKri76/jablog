package com.example.jablog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan("com.example.jablog.entity")
public class JablogApplication {

    public static void main(String[] args) {
        SpringApplication.run(JablogApplication.class, args);
    }

}
