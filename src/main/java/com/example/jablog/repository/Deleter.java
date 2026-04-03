package com.example.jablog.repository;

import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class Deleter {

    private final SessionFactory sessionFactory;

    public ArrayDeque<Posts> posts(long delta) {
        Session session = sessionFactory.getCurrentSession();
        final long expiredDate = Instant.now().toEpochMilli() - delta;

        String selectHql = "from Posts p where p.createdAt < :expiredDate";
        List<Posts> posts = session.createQuery(selectHql, Posts.class)
                .setParameter("expiredDate", expiredDate)
                .getResultList();

        String deleteHql = "delete Posts p where p.createdAt < :expiredDate";
        session.createQuery(deleteHql, Posts.class)
                .setParameter("expiredDate", expiredDate)
                .executeUpdate();

        return new ArrayDeque<Posts>(posts);
    }

    public ArrayDeque<Threads> threads(long delta){
        Session session = sessionFactory.getCurrentSession();
        final long expiredDate = Instant.now().toEpochMilli() - delta;

        String selectHql = "from Threads t where (select max(p.createdAt) from Posts where p.thread=t.id) < :expiredDate";
        List<Threads> threads = session.createQuery(selectHql, Threads.class)
                .setParameter("expiredDate", expiredDate)
                .getResultList();

        String deleteHql = "delete from Threads t where (select max(p.createdAt) from Posts where p.thread=t.id) < :expiredDate";
        session.createQuery(deleteHql, Threads.class)
                .setParameter("expiredDate", expiredDate)
                .executeUpdate();

        return new ArrayDeque<Threads>(threads);
    }
}
