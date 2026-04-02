package com.example.jablog.repository;

import com.example.jablog.entity.Board;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;

@Repository
public class GetterRepository {

    SessionFactory sessionFactory;

    @Transactional
    public  LinkedList<Board> start (){

        Session session = sessionFactory.openSession();
        LinkedList<Board> result = session.createQuery("from board", Board.class).getResultList();
    }
}
