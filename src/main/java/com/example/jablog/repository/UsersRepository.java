package com.example.jablog.repository;

import com.example.jablog.entity.Users;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UsersRepository {

    private final EntityManager entityManager;

    public void addUser(@NotNull Users users){
        entityManager.persist(users);
    }

    public void deleteUser (@NotNull String nickname, @NotNull String boardName){
        entityManager.createQuery("delete from Users u where u.nickname = :nickname and u.board.name = :boardName")
                .setParameter("nickname", nickname)
                .setParameter("boardName", boardName)
                .executeUpdate();
    }

    @NotNull
    public ArrayList<String> viewUsers(@NotNull String boardName){

        String jpql = "select u.nickname from Users u where u.board.name = :boardName";

        List<String> res = entityManager.createQuery(jpql, String.class)
                .setParameter("boardName", boardName)
                .getResultList();

        return new ArrayList<String>(res);
    }
}
