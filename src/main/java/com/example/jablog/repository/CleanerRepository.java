package com.example.jablog.repository;

import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CleanerRepository {

    private final EntityManager entityManager;

    public ArrayList<Posts> posts(long delta) {
        final long expiredDate = Instant.now().toEpochMilli() - delta;

        String selectJpql = "from Posts p where p.createdAt < :expiredDate";
        List<Posts> posts = entityManager.createQuery(selectJpql, Posts.class)
                .setParameter("expiredDate", expiredDate)
                .getResultList();

        String deleteJpql = "delete from Posts p where p.createdAt < :expiredDate";
        entityManager.createQuery(deleteJpql)
                .setParameter("expiredDate", expiredDate)
                .executeUpdate();

        return new ArrayList<Posts>(posts);
    }

    public ArrayList<Threads> threads(long delta, long oldThread){
        final long expiredDate = Instant.now().toEpochMilli() - delta;

        String selectJpql =
                "from Threads t " +
                "where not exists(" +
                        "select 1 from Posts p " +
                        "where p.createdAt >= :expiredDate " +
                        "and p.thread = t" +
                        ") " +
                        "and t.createdAt < :oldThread";
        List<Threads> threads = entityManager.createQuery(selectJpql, Threads.class)
                .setParameter("expiredDate", expiredDate)
                .setParameter("oldThread", oldThread)
                .getResultList();

        String deletePostsJpql =
                "delete from Posts p " +
                "where p.thread in (" +
                        "select t from Threads t " +
                        "where not exists(" +
                                "select 1 from Posts p2 " +
                                "where p2.createdAt >= :expiredDate " +
                                "and p2.thread = t" +
                                ") " +
                                "and t.createdAt < :oldThread" +
                        ")";
        entityManager.createQuery(deletePostsJpql)
                .setParameter("expiredDate", expiredDate)
                .setParameter("oldThread", oldThread)
                .executeUpdate();

        String deleteJpql =
                "delete from Threads t " +
                "where not exists(" +
                        "select 1 from Posts p " +
                        "where p.createdAt >= :expiredDate " +
                        "and p.thread = t" +
                        ") " +
                        "and t.createdAt < :oldThread";
        entityManager.createQuery(deleteJpql)
                .setParameter("expiredDate", expiredDate)
                .setParameter("oldThread", oldThread)
                .executeUpdate();

        return new ArrayList<Threads>(threads);
    }

    public ArrayList<String> pics(){

        ArrayList<String> pics = new ArrayList<>(1000);

        List<Threads> threads = entityManager.createQuery("from Threads t", Threads.class).getResultList();
        List<Posts> posts = entityManager.createQuery("from Posts p", Posts.class).getResultList();

        threads.forEach(thread ->
                pics.add(thread.getPicture())
                );
        posts.forEach(post ->
                pics.add(post.getPicture())
        );

        return pics;
    }

}
