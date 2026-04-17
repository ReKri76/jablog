package com.example.jablog.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class SecurityRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public String[] getRulesByBoardName(String boardName){
        return entityManager.createQuery("select b.rules from Board b where b.name = :boardName", String[].class)
                .setParameter("boardName", boardName)
                .getSingleResult();
    }
}
