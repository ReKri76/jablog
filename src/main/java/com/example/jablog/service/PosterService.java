package com.example.jablog.service;

import com.example.jablog.DTO.Picture;
import com.example.jablog.DTO.Post;
import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.entity.Users;
import com.example.jablog.repository.PosterRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PosterService {

    private static final String BUCKET = "images";
    public static final int NUMBER_OF_RULES_GROUPS = 3;
    public static final int SIZE_OF_GROUP = 4;
    public static final int SIZE_OF_ARRAY_OF_RULES = SIZE_OF_GROUP * NUMBER_OF_RULES_GROUPS;

    private final PosterRepository posterRepository;
    private final EntityManager entityManager;
    private final MinioService minioService;
    private final PasswordEncoder passwordEncoder;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Transactional
    public long thread(@NonNull Post post, Picture file, String board){

        if (post.getHead().isEmpty())
            post.setHead(post.getBody().substring(0,Math.min(120, post.getBody().length())));

        final Board boardRef = entityManager.unwrap(Session.class)
                .bySimpleNaturalId(Board.class)
                .getReference(board);

        final String name = buildPictureUrl(file.getName());

        final Threads threads = new Threads();
        threads.setContent(post.getBody());
        threads.setHeader(post.getHead());
        threads.setPicture(name);
        threads.setBoard(boardRef);

        final long idOfThread = posterRepository.thread(threads);

        minioService.savePicture(file, BUCKET);

        return idOfThread;
    }

    @Transactional
    public void post(@NonNull Post post, Picture file, long threadId, String board){

        String name = "";
        boolean savePic = false;
        if (file!=null) {
            name = buildPictureUrl(file.getName());
            savePic = true;
        }

        final Posts posts = new Posts();
        posts.setContent(post.getBody());
        posts.setHeader(post.getHead());
        posts.setThread(posterRepository.getThreadsById(threadId, board));
        posts.setPicture(name);

        posterRepository.post(posts);

        if (savePic)
            minioService.savePicture(file, BUCKET);
    }

    @Transactional
    public void board(String boardName, String password, String rule,
                      String nickname, int lifeCycleThreads , int lifeCyclePosts){

        if (lifeCyclePosts>=lifeCycleThreads)
            throw new RuntimeException("life cycle of posts cant be longer then threads");
        if (lifeCyclePosts<0)
            throw new RuntimeException("value of life cycle must be positive");

        if (rule.length()!=SIZE_OF_ARRAY_OF_RULES)
            throw new RuntimeException("incorrect rule");

        final String[] rules = new String[SIZE_OF_ARRAY_OF_RULES];
        for (int i = 0; i < rule.length(); i++) {
            rules[i] = String.valueOf(rule.charAt(i));
        }

        for (int i = 0; i < SIZE_OF_ARRAY_OF_RULES; i += SIZE_OF_GROUP){
            if (
                    switch (rules[i]) {
                        case "r" ->
                                !rules[i+1].equals("w") && !rules[i+1].equals("-") ||
                                !rules[i+2].equals("d") && !rules[i+2].equals("-") ||
                                !rules[i+3].equals("x") && !rules[i+3].equals("-");

                        case "-" -> {
                            for (int k = i; k < i + SIZE_OF_GROUP; k++)
                                if (!rules[k].equals("-"))
                                    yield true;
                            yield false;
                        }

                        default -> true;
                    }
            )
                throw new RuntimeException("incorrect rule");
        }

        final Board board = new Board();
        board.setName(boardName);
        board.setRules(rules);
        board.setLifeCyclePosts(lifeCyclePosts);
        board.setLifeCycleThreads(lifeCycleThreads);

        final Users users = new Users();
        users.setBoard(board);
        users.setRole(true);
        users.setPassword(passwordEncoder.encode(password));
        users.setNickname(nickname);

        posterRepository.board(board, users);
    }

    private String buildPictureUrl(String fileName) {
        return minioEndpoint.replaceAll("/+$", "") + "/" + BUCKET + "/" + fileName;
    }
}
