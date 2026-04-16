package com.example.jablog.repository;

import com.example.jablog.entity.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class APIRepository {

    private final EntityManager entityManager;

    @Transactional
    public Users login (String nickname) throws NoResultException {
        return entityManager.createQuery("from Users u where u.nickname = :nickname", Users.class)
                .setParameter("nickname", nickname)
                .getSingleResult();
    }

}
