package com.example.jablog.repository;

import com.example.jablog.service.PosterService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Repository
@RequiredArgsConstructor
public class PosterRepository {

    final private SessionFactory sessionFactory;

    public int thread(PosterService.Threads post, MultipartFile file){

        Session session = sessionFactory.openSession();
        Transaction trs = session.beginTransaction();

        Serializable id = session.

        session.close();
        return 0;
    }
}
