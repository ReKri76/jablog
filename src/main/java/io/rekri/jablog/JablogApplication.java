package io.rekri.jablog;

import io.rekri.jablog.config.MinioConfig;
import io.rekri.jablog.config.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan("io.rekri.jablog.entity")
@EnableScheduling
@Import({MinioConfig.class, SecurityConfig.class})
public class JablogApplication {

    public static void main(String[] args) {
        SpringApplication.run(JablogApplication.class, args);
    }

}