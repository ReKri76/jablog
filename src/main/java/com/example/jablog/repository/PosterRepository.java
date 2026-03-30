package com.example.jablog.repository;

import com.example.jablog.entity.PostBase;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
@RequiredArgsConstructor
public class PosterRepository {

    @Autowired
    MinioClient minioClient;

    final private SessionFactory sessionFactory;

    public long save(PostBase postBase, MultipartFile file){

        Session session = sessionFactory.openSession();
        Transaction trs = null;

        try {

            trs = session.beginTransaction();
            postBase.setPicture(savePicture(file));
            session.persist(postBase);
            trs.commit();
            return postBase.getId();

        } catch (Exception e) {
            if (trs!=null)
                trs.rollback();

        } finally {
            session.close();
        }

        throw new RuntimeException("error of save image");
    }

    private @NonNull String savePicture(MultipartFile pic){

        if (pic.isEmpty())
            return "placeholder";

        String bucket = "images";
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

        return "http://localhost:9000/" + bucket + "/" + name;
    }
}
