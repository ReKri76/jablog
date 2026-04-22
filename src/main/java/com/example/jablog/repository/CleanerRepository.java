package com.example.jablog.repository;

import com.example.jablog.entity.Board;
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

    private static final long UNIX_TIME_DAY = Duration.ofDays(1).toMillis();

    private final EntityManager entityManager;

    public ArrayList<Posts> posts() {
        final long now = Instant.now().toEpochMilli();

        final String selectJpql = "from Posts p where p.createdAt < :now - p.thread.board.lifeCyclePosts * :unxtm";
        final List<Posts> posts = entityManager.createQuery(selectJpql, Posts.class)
                .setParameter("now", now)
                .setParameter("unxtm", UNIX_TIME_DAY)
                .getResultList();

        final String deleteJpql = "delete from Posts p where p.createdAt < :now - p.thread.board.lifeCyclePosts * :unxtm";
        entityManager.createQuery(deleteJpql)
                .setParameter("now", now)
                .setParameter("unxtm", UNIX_TIME_DAY)
                .executeUpdate();

        return new ArrayList<Posts>(posts);
    }

    public ArrayList<Threads> threads(long oldThread){
        final long now = Instant.now().toEpochMilli();

        final String selectJpql =
                "from Threads t " +
                "where not exists(" +
                        "select 1 from Posts p " +
                        "where p.createdAt >= :now - t.board.lifeCycleThreads * :unxtm " +
                        "and p.thread = t" +
                        ") " +
                        "and t.createdAt < :oldThread";
        final List<Threads> threads = entityManager.createQuery(selectJpql, Threads.class)
                .setParameter("oldThread", oldThread)
                .setParameter("unxtm", UNIX_TIME_DAY)
                .setParameter("now", now)
                .getResultList();

        final String deleteJpql =
                "delete from Threads t " +
                "where not exists(" +
                        "select 1 from Posts p " +
                        "where p.createdAt >= :now - t.board.lifeCycleThreads * :unxtm " +
                        "and p.thread = t" +
                        ") " +
                        "and t.createdAt < :oldThread";
        entityManager.createQuery(deleteJpql)
                .setParameter("oldThread", oldThread)
                .setParameter("unxtm", UNIX_TIME_DAY)
                .setParameter("now", now)
                .executeUpdate();

        return new ArrayList<Threads>(threads);
    }

    public ArrayList<String> pics(){

        final List<String> pics = entityManager.createQuery("select t.picture from Threads t", String.class).getResultList();
        pics.addAll(entityManager.createQuery("select p.picture from Posts p", String.class).getResultList());

        return new ArrayList<String>(pics);
    }

    public ArrayList<Board> boards(long oldBoard){

        final String selectJpql = "from Board b left join fetch b.users where not exists(select 1 from Threads t where t.board = b) " +
                "and b.createdAt < :oldBoard";
        final List<Board> boards = entityManager.createQuery(selectJpql, Board.class)
                .setParameter("oldBoard", oldBoard)
                .getResultList();

        final String deleteJpql = "delete from Board b where not exists(select 1 from Threads t where t.board = b) " +
                "and b.createdAt < :oldBoard";
        entityManager.createQuery(deleteJpql, Board.class)
                .setParameter("oldBoard", oldBoard)
                .executeUpdate();

        return new ArrayList<Board>(boards);
    }

}
