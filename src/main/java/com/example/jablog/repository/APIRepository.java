package com.example.jablog.repository;

import com.example.jablog.entity.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class APIRepository {

    private final EntityManager entityManager;

    public Users login (String nickname) throws NoResultException {
        return entityManager.createQuery("from Users u where u.nickname = :nickname", Users.class)
                .setParameter("nickname", nickname)
                .getSingleResult();
    }

}
