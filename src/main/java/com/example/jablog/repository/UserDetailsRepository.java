package com.example.jablog.repository;

import com.example.jablog.entity.Users;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserDetailsRepository {

    private final EntityManager entityManager;

    public Users user (String username){
        return entityManager.createQuery("from Users u where u.nickname = :username", Users.class)
                .setParameter("username", username)
                .getSingleResult();
    }

}
