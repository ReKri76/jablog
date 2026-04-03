package com.example.jablog.repository;

import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayDeque;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GetterRepository {

    private final SessionFactory sessionFactory;
    private final static int limitOfPagination = 10;

    public ArrayDeque<Board> start(){

        Session session = sessionFactory.getCurrentSession();
        List<Board> result = session.createQuery("from Board", Board.class).getResultList();

        return new ArrayDeque<Board>(result);
    }

   public ArrayDeque<Threads> board(String boardName, int page){

       Session session = sessionFactory.getCurrentSession();
       String hql = "from Threads t where t.board = :boardName order by t.id desc";

       List<Threads> threads = session.createQuery(hql, Threads.class)
               .setParameter("boardName", boardName)
               .setFirstResult(page)
               .setMaxResults(limitOfPagination*page)
               .getResultList();

       return new ArrayDeque<Threads>(threads);
   }

    public ArrayDeque<Posts> thread(long threadId){

        Session session = sessionFactory.getCurrentSession();
        String hql = "from Posts p where p.thread = :threadId";

        List<Posts> posts = session.createQuery(hql, Posts.class)
                .setParameter("threadId", threadId)
                .getResultList();

        return new ArrayDeque<Posts>(posts);
    }
}
