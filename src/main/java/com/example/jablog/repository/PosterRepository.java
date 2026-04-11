package com.example.jablog.repository;

import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.entity.Users;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PosterRepository {

    private final EntityManager entityManager;

    @Transactional
    public long thread(Threads threads) {

        entityManager.persist(threads);
        entityManager.flush();

        return threads.getId();
    }

    @Transactional
    public void post(Posts posts) {entityManager.persist(posts);}

    @Transactional
    public void board(Board board, Users users){
        entityManager.persist(board);
        entityManager.persist(users);
    }

    @Transactional
    public void user(Users users){entityManager.persist(users);}
}
