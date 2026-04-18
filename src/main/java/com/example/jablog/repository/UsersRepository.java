package com.example.jablog.repository;

import com.example.jablog.entity.Users;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public ArrayList<String> viewUsers(String boardName){

        String jpql = "select u.nickname from Users u where u.board.name = :boardName";

        List<String> res = entityManager.createQuery(jpql, String.class)
                .setParameter("boardName", boardName)
                .getResultList();

        return new ArrayList<String>(res);
    }
}
