package com.example.jablog.service;

import com.example.jablog.DTO.Picture;
import io.minio.*;
import io.minio.messages.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Service
public class MinioService {

    private static final int MAX_PART_SIZE = 5 * 1024 * 1024;
    private static final int INITIAL_FILE_LIST_CAPACITY = 1000;
    public static final String BUCKET = "images";
    public static final String DEFAULT_BUCKET = BUCKET;

    private final String endpoint;
    private final MinioClient minioClient;

    public MinioService(@Value("${minio.endpoint:DEBUG_NOT_FOUND}") String endpoint,
                        MinioClient privateMinioClient) {
        this.endpoint = endpoint;
        this.minioClient = privateMinioClient;
    }

    public void savePicture(@NotNull Picture pic, @Nullable String bucket) throws RuntimeException{

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

    public void deletePicture(@NotNull String pathToPic) throws RuntimeException{

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
    public ArrayList<String> getAllFileName(@NotNull String bucket) throws RuntimeException{

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
    public InputStream getFile(@NotNull String fileName){
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                            .bucket(BUCKET)
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error when getting file: " + e);
        }
    }

    @Nullable
    public String buildPictureUrl(@Nullable String fileName, @NotNull String boardName) {
        if (fileName == null)
            return null;

        return "/" + boardName + "/img/"+ URLEncoder.encode(fileName, StandardCharsets.UTF_8);
    }
}