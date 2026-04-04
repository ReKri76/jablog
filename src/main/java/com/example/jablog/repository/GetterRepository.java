package com.example.jablog.repository;

import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayDeque;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GetterRepository {

    private final EntityManager entityManager;
    private final static int limitOfPagination = 10;

    @Transactional
    public ArrayDeque<Board> start(){

        List<Board> result = entityManager.createQuery("from Board", Board.class).getResultList();

        return new ArrayDeque<Board>(result);
    }

    @Transactional
   public ArrayDeque<Threads> board(String boardName, int page){

       String hql = "from Threads t where t.board = :boardName order by t.id desc";

       List<Threads> threads = entityManager.createQuery(hql, Threads.class)
               .setParameter("boardName", boardName)
               .setFirstResult(page*limitOfPagination)
               .setMaxResults(limitOfPagination)
               .getResultList();

       return new ArrayDeque<Threads>(threads);
   }

   @Transactional
   public ArrayDeque<Posts> thread(long threadId){

        String hql = "from Posts p where p.thread = :threadId order by p.id";

        List<Posts> posts = entityManager.createQuery(hql, Posts.class)
                .setParameter("threadId", threadId)
                .getResultList();

        return new ArrayDeque<Posts>(posts);
    }
}
