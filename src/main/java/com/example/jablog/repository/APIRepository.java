package com.example.jablog.repository;

import com.example.jablog.entity.Threads;
import com.example.jablog.entity.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class APIRepository {

    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Users login (String nickname) throws NoResultException {
        return entityManager.createQuery("from Users u where u.nickname = :nickname", Users.class)
                .setParameter("nickname", nickname)
                .getSingleResult();
    }

    @Transactional
    public Threads likeThread(int threadId){
        entityManager.createQuery("update Threads t set t.carma = t.carma+1 where t.id = :threadId")
                .setParameter("threadId", threadId)
                .executeUpdate();

        return entityManager.createQuery("from Threads t where t.id = :threadId", Threads.class)
                .setParameter("threadId", threadId)
                .getSingleResult();
    }

    @Transactional
    public Threads dislikeThread(int threadId){
        entityManager.createQuery("update Threads t set t.carma = t.carma-1 where t.id = :threadId")
                .setParameter("threadId", threadId)
                .executeUpdate();

        return entityManager.createQuery("from Threads t where t.id = :threadId", Threads.class)
                .setParameter("threadId", threadId)
                .getSingleResult();
    }
}
