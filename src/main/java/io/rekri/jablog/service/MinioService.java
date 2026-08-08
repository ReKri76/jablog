package io.rekri.jablog.service;

import io.rekri.jablog.DTO.Picture;
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
import java.util.List;

@Service
public class MinioService {

    private static final int MAX_PART_SIZE = 5 * 1024 * 1024;
    private static final int INITIAL_FILE_LIST_CAPACITY = 1000;
    public static final String BUCKET = "images";

    private final String endpoint;
    private final MinioClient minioClient;

    public MinioService(@Value("${minio.endpoint:DEBUG_NOT_FOUND}") String endpoint,
                        MinioClient privateMinioClient) {
        this.endpoint = endpoint;
        this.minioClient = privateMinioClient;
    }

    public void savePicture(@NotNull Picture pic, @Nullable String bucket) throws RuntimeException{

        if (bucket==null)
            bucket=BUCKET;

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

    public void deletePicture(@NotNull String fileName, @Nullable String bucket) throws RuntimeException{

        if (bucket==null)
            bucket=BUCKET;

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("error to delete file:" , e);
        }
    }

    @NotNull
    public List<String> getAllFileName(@NotNull String bucket) throws RuntimeException{

        final List<String> names = new ArrayList<String>(INITIAL_FILE_LIST_CAPACITY);

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