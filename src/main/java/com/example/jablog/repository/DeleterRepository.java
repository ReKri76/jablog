package com.example.jablog.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeleterRepository {

    private final EntityManager entityManager;

    public void thread(long threadId){

        entityManager.createQuery("delete from Threads t where t.id = :threadId")
                .setParameter("threadId", threadId)
                .executeUpdate();
    }

    public void post(long postId){

        entityManager.createQuery("delete from Posts p where p.id = :postId")
                .setParameter("postId", postId)
                .executeUpdate();
    }
}
