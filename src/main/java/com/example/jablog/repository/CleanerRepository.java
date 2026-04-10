package com.example.jablog.repository;

import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CleanerRepository {

    private final EntityManager entityManager;
    private final long unxtm = Duration.ofDays(1).toMillis();

    public ArrayList<Posts> posts() {
        final long now = Instant.now().toEpochMilli();

        String selectJpql = "from Posts p where p.createdAt < :now - p.thread.board.lifeCyclePosts * :unxtm";
        List<Posts> posts = entityManager.createQuery(selectJpql, Posts.class)
                .setParameter("now", now)
                .setParameter("unxtm", unxtm)
                .getResultList();

        String deleteJpql = "delete from Posts p where p.createdAt < :now - p.thread.board.lifeCyclePosts * :unxtm";
        entityManager.createQuery(deleteJpql)
                .setParameter("now", now)
                .setParameter("unxtm", unxtm)
                .executeUpdate();

        return new ArrayList<Posts>(posts);
    }

    public ArrayList<Threads> threads(long oldThread){
        final long now = Instant.now().toEpochMilli();

        String selectJpql =
                "from Threads t " +
                "where not exists(" +
                        "select 1 from Posts p " +
                        "where p.createdAt >= :now - t.board.lifeCycleThreads * :unxtm " +
                        "and p.thread = t" +
                        ") " +
                        "and t.createdAt < :oldThread";
        List<Threads> threads = entityManager.createQuery(selectJpql, Threads.class)
                .setParameter("oldThread", oldThread)
                .setParameter("unxtm", unxtm)
                .setParameter("now", now)
                .getResultList();

        String deleteJpql =
                "delete from Threads t " +
                "where not exists(" +
                        "select 1 from Posts p " +
                        "where p.createdAt >= now - t.board.lifeCycleThreads * :unxtm " +
                        "and p.thread = t" +
                        ") " +
                        "and t.createdAt < : oldThread";
        entityManager.createQuery(deleteJpql)
                .setParameter("oldThread", oldThread)
                .setParameter("unxtm", unxtm)
                .setParameter("now", now)
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
