package com.example.jablog.repository;

import com.example.jablog.entity.Board;
import com.example.jablog.entity.Threads;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GetterRepository {

    private static final int LIMIT_OF_PAGINATION = 10;

    private final EntityManager entityManager;

    public ArrayList<Board> start(){

        final List<Board> result = entityManager.createQuery("from Board", Board.class).getResultList();

        return new ArrayList<Board>(result);
    }

   public ArrayList<Threads> board(String boardName, int page){

       final String jpql = "from Threads t where t.board.name = :boardName order by t.id desc";

       final List<Threads> threads = entityManager.createQuery(jpql, Threads.class)
               .setParameter("boardName", boardName)
               .setFirstResult(page * LIMIT_OF_PAGINATION)
               .setMaxResults(LIMIT_OF_PAGINATION)
               .getResultList();

       return new ArrayList<Threads>(threads);
   }

   public Threads thread(long threadId, String boardName){

        final String jpql = "from Threads t left join fetch t.posts where t.id = :threadId and t.board.name = :boardName";

       final Threads posts = entityManager.createQuery(jpql, Threads.class)
                .setParameter("threadId", threadId)
                .setParameter("boardName", boardName)
               .getSingleResult();

        return posts;
    }
}
