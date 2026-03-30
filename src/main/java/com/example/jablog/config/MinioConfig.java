package com.example.jablog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.minio.MinioClient;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minio", "634289761")
                .build();
    }
}
