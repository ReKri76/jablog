package com.example.jablog.service;

import ch.qos.logback.classic.Logger;
import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.repository.CleanerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class Cleaner {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(Cleaner.class);
    private static final String BUCKET = "images";

    private final CleanerRepository cleanerRepository;
    private final MinioService minioService;

    @Scheduled(cron = "0 0 4 * * WED")
    public void cleanThreads(){

        final long oldThread = Instant.now().toEpochMilli()- Duration.ofMinutes(30).toMillis();

        final ArrayList<Threads> deletedThreads = cleanerRepository.threads(oldThread);

        deletedThreads.forEach( thread -> minioService.deletePicture(thread.getPicture()));

        LOGGER.info("{} threads deleted in {}.", deletedThreads.size(), LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 4 * * MON")
    public void cleanPosts(){

        final ArrayList<Posts> deletedPosts = cleanerRepository.posts();

        deletedPosts.forEach(post->{
            final String url = post.getPicture();
            if (!url.isEmpty())
                minioService.deletePicture(url);
        });

        LOGGER.info("{} posts deleted in {}.", deletedPosts.size(), LocalDateTime.now());
    }

    @Scheduled(cron ="0 0 4 13 * *")
    public void cleanPics(){

        final ArrayList<String> pics = cleanerRepository.pics();
        final ArrayList<String> realPics = minioService.getAllFileName(BUCKET);

        realPics.forEach( pic -> {
            if (!pics.contains(pic))
                minioService.deletePicture(pic);
        });
    }

    @Scheduled(cron="0 0 5 24 * *")
    public void cleanBoards(){

        final long oldBoard = Instant.now().toEpochMilli()-Duration.ofDays(1).toMillis();

        final ArrayList<Board> deletedBoards = cleanerRepository.boards(oldBoard);

        deletedBoards.forEach(board ->
            LOGGER.info("{} board was deleted. This board has a {} users", board.getName(), board.getUsers().size())
        );

        LOGGER.info("{} boards deleted in {}.", deletedBoards.size(), LocalDateTime.now());

    }
}
