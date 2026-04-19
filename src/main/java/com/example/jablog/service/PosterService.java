package com.example.jablog.service;

import com.example.jablog.DTO.Picture;
import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.entity.Users;
import com.example.jablog.repository.PosterRepository;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.jablog.DTO.Post;

@Service
@RequiredArgsConstructor
public class PosterService {

    private final String bucket = "images";
    private final int sizeOfArrayOfRules = 12;

    private final PosterRepository posterRepository;
    private final EntityManager entityManager;
    private final MinioService minioService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public long thread(@NonNull Post post, Picture file, String board){

        if (post.getHead().isEmpty())
            post.setHead(post.getBody().substring(0,Math.min(120, post.getBody().length())));

        final Board boardRef = entityManager.unwrap(Session.class)
                .bySimpleNaturalId(Board.class)
                .getReference(board);

        minioService.savePicture(file, bucket);

        final String name = System.getenv("MINIO_ENDPOINT")+bucket+"/"+file.getName();

        final Threads threads = new Threads();
        threads.setContent(post.getBody());
        threads.setHeader(post.getHead());
        threads.setPicture(name);
        threads.setBoard(boardRef);

        final long idOfThread = posterRepository.thread(threads);

        return idOfThread;
    }

    @Transactional
    public void post(@NonNull Post post, Picture file, long threadId){

        String name = "";
        if (file!=null) {
            minioService.savePicture(file, bucket);
            name = System.getenv("MINIO_ENDPOINT")+bucket+"/"+file.getName();
        }

        final Posts posts = new Posts();
        posts.setContent(post.getBody());
        posts.setHeader(post.getHead());
        posts.setThread(entityManager.getReference(Threads.class, threadId));
        posts.setPicture(name);

        posterRepository.post(posts);
    }

    @Transactional
    public void board(String boardName, String password, String rule,
                      String nickname, int lifeCycleThreads , int lifeCyclePosts){

        if (lifeCyclePosts>=lifeCycleThreads)
            throw new RuntimeException("life cycle of posts cant be longer then threads");
        if (lifeCyclePosts<0)
            throw new RuntimeException("value of life cycle must be positive");

        if (rule.length()!=sizeOfArrayOfRules)
            throw new RuntimeException("incorrect rule");

        String[] rules = new String[sizeOfArrayOfRules];

        for (int i = 0; i<rule.length(); i++){
            char currentValue = rule.charAt(i);
            if (currentValue != 'w' && currentValue !='r' && currentValue != '-'
                    && currentValue != 'x' && currentValue != 'd')
                throw new RuntimeException("incorrect rule");
            rules[i]=String.valueOf(currentValue);
        }

        Board board = new Board();
        board.setName(boardName);
        board.setRules(rules);
        board.setLifeCyclePosts(lifeCyclePosts);
        board.setLifeCycleThreads(lifeCycleThreads);

        Users users = new Users();
        users.setBoard(board);
        users.setRole(true);
        users.setPassword(passwordEncoder.encode(password));
        users.setNickname(nickname);

        posterRepository.board(board, users);
    }


}
