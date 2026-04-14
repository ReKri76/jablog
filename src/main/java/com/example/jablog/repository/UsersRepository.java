package com.example.jablog.repository;

import com.example.jablog.entity.Users;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UsersRepository {

    private final EntityManager entityManager;

    @Transactional
    public void addUser(Users users){
        entityManager.persist(users);
    }

    @Transactional
    public void deleteUser (String nickname){
        entityManager.createQuery("delete from Users u where u.nickname = :nickname")
                .setParameter("nickname", nickname)
                .executeUpdate();
    }
}
