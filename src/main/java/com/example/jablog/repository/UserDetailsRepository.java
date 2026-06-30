package com.example.jablog.repository;

import com.example.jablog.entity.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserDetailsRepository {

    private final EntityManager entityManager;

    @NotNull
    public Users user (@NotNull String username) throws NoResultException {
        return entityManager.createQuery("from Users u where u.nickname = :username", Users.class)
                .setParameter("username", username)
                .getSingleResult();
    }
}
