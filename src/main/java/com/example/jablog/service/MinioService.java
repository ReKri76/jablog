package com.example.jablog.service;

import com.example.jablog.DTO.Picture;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MinioService {

    private static final int MAX_PART_SIZE = 10 * 1024 * 1024;
    private static final int INITIAL_FILE_LIST_CAPACITY = 1000;
    public static final String BUCKET = "images";

    @Value("${minio.endpoint:DEBUG_NOT_FOUND}")
    public static  String ENDPOINT;

    private final MinioClient minioClient;

    public void savePicture(@NonNull Picture pic, String bucket){

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

    public void deletePicture(@NonNull String pathToPic){

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

    public ArrayList<String> getAllFileName(String bucket){

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

    public static String buildPictureUrl(String fileName) {
        return ENDPOINT.replaceAll("/+$", "") + "/" + BUCKET + "/" + fileName;
    }

}
