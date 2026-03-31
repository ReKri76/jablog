package com.example.jablog.repository;

import com.example.jablog.entity.PostBase;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
@RequiredArgsConstructor
public class PosterRepository {

    @Autowired
    MinioClient minioClient;

    final private SessionFactory sessionFactory;

    @Transactional
    public long save(PostBase postBase, MultipartFile file, String bucket){

        Session session = sessionFactory.openSession();

        try {
            savePicture(file, bucket);
            session.persist(postBase);
            session.flush();
        } finally {
            session.close();
        }
        return postBase.getId();
    }

    private void savePicture(MultipartFile pic, String bucket){

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
}
