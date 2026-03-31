package com.example.jablog.service;

import com.example.jablog.controllers.Poster;
import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.repository.PosterRepository;
import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PosterService {

    String bucket = "images";

    @Autowired
    PosterRepository posterRepository;
    @Autowired
    private EntityManager entityManager;


    public long thread(Poster.@NonNull Post post, MultipartFile file, String board){

        if (post.getHead().isEmpty())
            post.setHead(post.getBody().substring(0,120));

        Board boardRef = entityManager.unwrap(org.hibernate.Session.class)
                .bySimpleNaturalId(Board.class)
                .getReference(board);

        String name = "http://localhost:9000["+file.getOriginalFilename()+"]["+System.currentTimeMillis()+"]";

        Threads threads = new Threads();
        threads.setContent(post.getBody());
        threads.setHeader(post.getHead());
        threads.setBoard(entityManager.getReference(Board.class, board));
        threads.setPicture(name);
        threads.setBoard(boardRef);

        long idOfThread = posterRepository.save(threads, file, bucket);

        return idOfThread;
    }

    public long post(Poster.@NonNull Post post, MultipartFile file, long threadId){

        if (post.getHead().isEmpty())
            post.setHead(post.getBody().substring(0,120));

        String name = "http://localhost:9000["+file.getOriginalFilename()+"]["+System.currentTimeMillis()+"]";

        Posts posts = new Posts();
        posts.setContent(post.getBody());
        posts.setHeader(post.getHead());
        posts.setThread(entityManager.getReference(Threads.class, threadId));
        posts.setPicture(name);


        long idOfThread = posterRepository.save(posts, file, bucket);

        return idOfThread;
    }
}
