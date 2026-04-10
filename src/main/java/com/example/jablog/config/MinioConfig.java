package com.example.jablog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.minio.MinioClient;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
                .endpoint(System.getenv("MINIO_ENDPOINT"))
                .credentials(System.getenv("MINIO_ACCESS_KEY"), System.getenv("MINIO_SECRET_KEY"))
                .build();
    }
}
