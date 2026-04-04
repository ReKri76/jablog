package com.example.jablog.repository;

import com.example.jablog.entity.PostBase;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PosterRepository {

    private final EntityManager entityManager;

    @Transactional
    public long save(PostBase postBase) {

        entityManager.persist(postBase);
        entityManager.flush();

        return postBase.getId();
    }
}
