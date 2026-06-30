package com.example.jablog.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SecurityRepository {

    final private EntityManager entityManager;

    @NotNull
    public String getRulesByBoardName(String boardName){
        return entityManager.createQuery("select b.rules from Board b where b.name = :boardName", String.class)
                .setParameter("boardName", boardName)
                .getSingleResult();
    }


    public boolean isThreadInBoard(String boardName, long threadId){
        Long count = entityManager.createQuery("""
            select count(t)
            from Threads t
            where t.id = :threadId
              and t.board.name = :boardName
            """, Long.class)
                .setParameter("boardName", boardName)
                .setParameter("threadId", threadId)
                .getSingleResult();

        return count > 0;
    }

    public boolean isPostInBoard(String boardName, long postId){
        Long count = entityManager.createQuery("""
            select count(t)
            from Posts t
            where t.id = :postId
              and t.thread.board.name = :boardName
            """, Long.class)
                .setParameter("boardName", boardName)
                .setParameter("postId", postId)
                .getSingleResult();

        return count > 0;
    }
}
