package com.example.jablog.repository;

import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class Deleter {

    private final EntityManager entityManager;

    public ArrayDeque<Posts> posts(long delta) {
        final long expiredDate = Instant.now().toEpochMilli() - delta;

        String selectHql = "from Posts p where p.createdAt < :expiredDate";
        List<Posts> posts = entityManager.createQuery(selectHql, Posts.class)
                .setParameter("expiredDate", expiredDate)
                .getResultList();

        String deleteHql = "delete from Posts p where p.createdAt < :expiredDate";
        entityManager.createQuery(deleteHql)
                .setParameter("expiredDate", expiredDate)
                .executeUpdate();

        return new ArrayDeque<Posts>(posts);
    }

    public ArrayDeque<Threads> threads(long delta){
        final long expiredDate = Instant.now().toEpochMilli() - delta;

        String selectHql =
                "from Threads t " +
                "where not exists(" +
                        "select 1 from Posts p " +
                        "where p.createdAt >= :expiredDate " +
                        "and p.thread = t" +
                        ")";
        List<Threads> threads = entityManager.createQuery(selectHql, Threads.class)
                .setParameter("expiredDate", expiredDate)
                .getResultList();

        String deleteHql =
                "delete from Threads t " +
                "where not exists(" +
                        "select 1 from Posts p " +
                        "where p.createdAt >= :expiredDate " +
                        "and p.thread = t" +
                        ")";
        entityManager.createQuery(deleteHql)
                .setParameter("expiredDate", expiredDate)
                .executeUpdate();

        return new ArrayDeque<Threads>(threads);
    }
}
