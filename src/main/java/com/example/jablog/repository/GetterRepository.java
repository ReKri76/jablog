package com.example.jablog.repository;

import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
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
   public ArrayList<Posts> thread(long threadId, String boardName){

        String jpql = "from Posts p where p.thread.id = :threadId and p.thread.board.name = :boardName order by p.id";

        List<Posts> posts = entityManager.createQuery(jpql, Posts.class)
                .setParameter("threadId", threadId)
                .setParameter("boardName", boardName)
                .getResultList();

        return new ArrayList<Posts>(posts);
    }

    @Transactional
    public Threads getThread(long threadId){
        return  entityManager.find(Threads.class, threadId);
    }
}
