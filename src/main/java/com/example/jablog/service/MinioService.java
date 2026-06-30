package com.example.jablog.service;

import com.example.jablog.DTO.Picture;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    private static final int MAX_PART_SIZE = 5 * 1024 * 1024;
    private static final int INITIAL_FILE_LIST_CAPACITY = 1000;
    public static final String BUCKET = "images";
    public static final String DEFAULT_BUCKET = BUCKET;

    private final String endpoint;
    private final MinioClient minioClient;

    public MinioService(@Value("${minio.endpoint:DEBUG_NOT_FOUND}") String endpoint, MinioClient minioClient) {
        this.endpoint = endpoint;
        this.minioClient = minioClient;
    }

    public void savePicture(@NotNull Picture pic, @Nullable String bucket){

        if  (bucket==null)
            bucket=DEFAULT_BUCKET;

        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(pic.getName())
                    .stream(pic.getInputStream(), pic.getSize(), MAX_PART_SIZE)
                    .contentType(pic.getContentType())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("error of save image:", e);
        }
    }

    public void deletePicture(@NotNull String pathToPic){

        final String[] parts = pathToPic.split("/");
        final String objectName = parts[parts.length - 1];
        final String bucketName = parts[parts.length - 2];

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("error to delete file:" , e);
        }
    }

    @NotNull
    public ArrayList<String> getAllFileName(@NotNull String bucket){

        final ArrayList<String> names = new ArrayList<String>(INITIAL_FILE_LIST_CAPACITY);

        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucket)
                .recursive(true)
                .build());

        results.forEach( result ->{
            try{
                final Item item = result.get();
                names.add(item.objectName());
            } catch (Exception e){
                throw new RuntimeException("cant find files: ", e);
            }
        });
        return names;
    }

    @NotNull
    public String buildPictureUrl(@NotNull String fileName) {
        try {
            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(BUCKET)
                    .object(fileName)
                    .expiry(1, TimeUnit.MINUTES)
                    .build();

            return minioClient.getPresignedObjectUrl(args);

        } catch (Exception e) {
            throw new RuntimeException("Error when creating link:", e);
        }
    }
}