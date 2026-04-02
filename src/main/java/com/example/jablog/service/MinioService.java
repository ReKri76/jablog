package com.example.jablog.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    public void savePicture(@NonNull MultipartFile pic, String bucket){

        if (pic.isEmpty())
            throw new RuntimeException("cannot upload image");

        String name = "["+pic.getOriginalFilename()+"]["+System.currentTimeMillis()+"]";

        try {
            minioClient.putObject(PutObjectArgs.builder().
                    bucket(bucket).
                    object(name).
                    stream(pic.getInputStream(), pic.getSize(), 10*1024*1024).
                    contentType(pic.getContentType()).
                    build());
        } catch (Exception e) {
            throw new RuntimeException("error of save image");
        }
    }

    public void deletePicture(@NonNull String pathToPic){

    }

}

