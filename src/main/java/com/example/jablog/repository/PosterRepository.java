package com.example.jablog.repository;

import com.example.jablog.entity.PostBase;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PosterRepository {

    final private SessionFactory sessionFactory;

    public long save(PostBase postBase) {

        Session session = sessionFactory.getCurrentSession();

        session.persist(postBase);
        session.flush();

        return postBase.getId();
    }
}
