package com.example.jablog.service;

import com.example.jablog.DTO.Picture;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    public void savePicture(@NonNull Picture pic, String bucket){

        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(pic.getName())
                    .stream(pic.getInputStream(), pic.getSize(), 10*1024*1024)
                    .contentType(pic.getContentType())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("error of save image:", e);
        }
    }

    public void deletePicture(@NonNull String pathToPic){

        String[] parts = pathToPic.split("/");
        String objectName = parts[parts.length-1];
        String bucketName = parts[parts.length-2];

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

        ArrayList<String> names = new ArrayList<String>(1000);

        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucket)
                .recursive(true)
                .build());

        results.forEach( result ->{
            try{
                Item item = result.get();
                names.add(item.objectName());
            } catch (Exception e){
                throw new RuntimeException("cant find files: ", e);
            }
        });
        return names;
    }

}

