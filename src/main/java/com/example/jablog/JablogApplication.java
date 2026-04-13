package com.example.jablog;

import com.example.jablog.config.MinioConfig;
import com.example.jablog.config.MvcConfig;
import com.example.jablog.config.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan("com.example.jablog.entity")
@EnableScheduling
@Import({MinioConfig.class, SecurityConfig.class, MvcConfig.class})
public class JablogApplication {

    public static void main(String[] args) {
        SpringApplication.run(JablogApplication.class, args);
    }

}
