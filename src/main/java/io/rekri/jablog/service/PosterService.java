package io.rekri.jablog.service;

import io.rekri.jablog.DTO.BoardToCreate;
import io.rekri.jablog.DTO.Picture;
import io.rekri.jablog.DTO.Post;
import io.rekri.jablog.entity.Board;
import io.rekri.jablog.entity.Posts;
import io.rekri.jablog.entity.Threads;
import io.rekri.jablog.entity.Users;
import io.rekri.jablog.errors.InvalidRulesException;
import io.rekri.jablog.repository.PosterRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PosterService {

    private final PosterRepository posterRepository;
    private final EntityManager entityManager;
    private final MinioService minioService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public long thread(@NotNull @Valid Post post, @NotNull Picture file, @NotNull String board){

        if (file == null)
            throw new IllegalArgumentException();

        if (post.getHead() == null || post.getHead().isEmpty())
            post.setHead(post.getBody().substring(0,Math.min(120, post.getBody().length())));

        final Board boardRef = entityManager.unwrap(Session.class)
                .bySimpleNaturalId(Board.class)
                .getReference(board);

        final String name = file.getName();

        final Threads threads = new Threads();
        threads.setContent(post.getBody());
        threads.setHeader(post.getHead());
        threads.setCarma(0);
        threads.setPicture(name);
        threads.setBoard(boardRef);

        final long idOfThread = posterRepository.thread(threads);

        minioService.savePicture(file, MinioService.BUCKET);

        return idOfThread;
    }

    @Transactional
    public void post(@NotNull Post post, @Nullable Picture file, long threadId){

        String name = "";
        boolean savePic = false;
        if (file!=null) {
            name = file.getName();
            savePic = true;
        }

        final Posts posts = new Posts();
        posts.setContent(post.getBody());
        posts.setHeader(post.getHead());
        posts.setThread(posterRepository.getThreadsById(threadId));
        posts.setPicture(name);

        posterRepository.post(posts);

        if (savePic)
            minioService.savePicture(file, MinioService.BUCKET);
    }

    /**
     * @see {@link Board}
     * */
    @Transactional
    public void board(BoardToCreate inputBoard){
        log.info("Start creating board");

        var lifeCyclePosts = inputBoard.getLifeCyclePosts();
        var lifeCycleThreads = inputBoard.getLifeCycleThreads();
        var rule = inputBoard.getRule();
        var transcription = inputBoard.getTranscription();
        var boardName = inputBoard.getBoardName();
        var password = inputBoard.getPass();
        var nickname = inputBoard.getNickname();

        if (lifeCyclePosts>=lifeCycleThreads)
            throw new InvalidRulesException("life cycle of posts cant be longer then threads");
        if (lifeCyclePosts<0)
            throw new InvalidRulesException("value of life cycle must be positive");
        if(lifeCycleThreads>Board.MAX_LIFE_CYCLE_OF_THREADS)
            throw new InvalidRulesException("value of life cycle of threads cant be more than 28");

        if (rule.length()!=Board.SIZE_OF_ARRAY_OF_RULES)
            throw new InvalidRulesException("incorrect rule");

        for (int i = 0; i < Board.SIZE_OF_ARRAY_OF_RULES; i += Board.SIZE_OF_GROUP){
            if (
                    switch (rule.charAt(i)) {
                        case 'r' ->
                                (rule.charAt(i + 1) != 'w' && rule.charAt(i + 1) != '-') ||
                                (rule.charAt(i + 2) != 'd' && rule.charAt(i + 2) != '-') ||
                                (rule.charAt(i + 3) != 'x' && rule.charAt(i + 3) != '-');

                        case '-' -> {
                            for (int k = i; k < i + Board.SIZE_OF_GROUP; k++)
                                if (rule.charAt(k) != '-')
                                    yield true;
                            yield false;
                        }

                        default -> true;
                    }
            ){
                log.warn("Invalid board rule : {}", rule);
                throw new InvalidRulesException("incorrect board rule");
            }
        }

        if (transcription == null || transcription.isEmpty())
            transcription=boardName;

        final Board board = new Board();
        board.setName(boardName);
        board.setRules(rule);
        board.setLifeCyclePosts(lifeCyclePosts);
        board.setLifeCycleThreads(lifeCycleThreads);
        board.setTranscription(transcription);

        final Users users = new Users();
        users.setBoard(board);
        users.setRole(true);
        users.setPassword(passwordEncoder.encode(password));
        users.setNickname(nickname);

        posterRepository.board(board, users);

        log.info("Board {} is created", boardName);
    }
}
