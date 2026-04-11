package com.example.jablog.repository;

import com.example.jablog.entity.Board;
import com.example.jablog.entity.Threads;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GetterRepository {

    private final EntityManager entityManager;
    private final int limitOfPagination = 10;

    @Transactional
    public ArrayList<Board> start(){

        List<Board> result = entityManager.createQuery("from Board", Board.class).getResultList();

        return new ArrayList<Board>(result);
    }

    @Transactional
   public ArrayList<Threads> board(String boardName, int page){

       String jpql = "from Threads t where t.board.name = :boardName order by t.id desc";

       List<Threads> threads = entityManager.createQuery(jpql, Threads.class)
               .setParameter("boardName", boardName)
               .setFirstResult(page*limitOfPagination)
               .setMaxResults(limitOfPagination)
               .getResultList();

       return new ArrayList<Threads>(threads);
   }

   @Transactional
   public Threads thread(long threadId, String boardName){

        String jpql = "from Threads t left join fetch t.posts where t.id = :threadId and t.board.name = :boardName";

       Threads posts = entityManager.createQuery(jpql, Threads.class)
                .setParameter("threadId", threadId)
                .setParameter("boardName", boardName)
               .getSingleResult();

        return posts;
    }
}
