package com.example.jablog.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeleterRepository {

    private final EntityManager entityManager;

    public void thread(long threadId, String boardName){

        entityManager.createQuery("delete from Threads t where t.id = :threadId and t.board.name = :boardName")
                .setParameter("threadId", threadId)
                .setParameter("boardName", boardName)
                .executeUpdate();
    }

    public void post(long postId, String boardName){

        entityManager.createQuery("delete from Posts p where p.id = :postId and p.thread.board.name = :boardName")
                .setParameter("postId", postId)
                .setParameter("boardName", boardName)
                .executeUpdate();
    }
}
