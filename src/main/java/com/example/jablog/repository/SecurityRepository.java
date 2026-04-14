package com.example.jablog.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SecurityRepository {

    private final EntityManager entityManager;

    public String[] getRulesByBoardName(String boardName){
        return entityManager.createQuery("select b.rules from Board b where b.name = :boardName", String[].class)
                .setParameter("boardName", boardName)
                .getSingleResult();
    }
}
