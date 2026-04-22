package com.example.jablog.repository;

import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.entity.Users;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PosterRepository {

    private final EntityManager entityManager;

    public long thread(Threads threads) {

        entityManager.persist(threads);
        entityManager.flush();

        return threads.getId();
    }

    public void post(Posts posts) {entityManager.persist(posts);}

    public void board(Board board, Users users){
        entityManager.persist(board);
        entityManager.persist(users);
    }

    public void user(Users users){entityManager.persist(users);}

    public Threads getThreadsById(long id, String boardName){
        return entityManager.createQuery("from Threads t where t.id = :threadId and t.board.name = :boardName",
                        Threads.class)
                 .setParameter("threadId", id)
                 .setParameter("boardName", boardName)
                 .getSingleResult();
     }
}
