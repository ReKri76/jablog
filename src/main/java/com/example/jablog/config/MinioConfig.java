package com.example.jablog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.minio.MinioClient;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(
            @Value("${minio.endpoint:DEBUG_NOT_FOUND}") String endpoint,
            @Value("${minio.access.key}") String accessKey,
            @Value("${minio.secret.key}") String secretKey) {

        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
