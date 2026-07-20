package com.example.jablog.repository;

import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeleterRepository {

    private final EntityManager entityManager;

    @NotNull
    public Threads thread(long threadId){

        Threads res = entityManager.find(Threads.class,threadId);

        entityManager.createQuery("delete from Threads t where t.id = :threadId")
                .setParameter("threadId", threadId)
                .executeUpdate();

        return res;
    }

    @NotNull
    public Posts post(long postId){

        Posts res = entityManager.find(Posts.class,postId);

        entityManager.createQuery("delete from Posts p where p.id = :postId")
                .setParameter("postId", postId)
                .executeUpdate();

        return res;
    }
}
