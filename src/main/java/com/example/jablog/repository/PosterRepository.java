package com.example.jablog.repository;

import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
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
    public void post(Posts posts) {

        entityManager.persist(posts);
        entityManager.flush();
    }

    @Transactional
    public void board(String boardName, String password, String rule){

    }
}
