package com.example.jablog.repository;

import com.example.jablog.DTO.CleanPostRecord;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class Deleter {

    private final SessionFactory sessionFactory;

    public CleanPostRecord posts(long delta){
        Session session = sessionFactory.openSession();

    }
}
