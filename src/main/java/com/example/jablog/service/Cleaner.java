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

    private final CleanerRepository cleanerRepository;
    private final static Logger logger = (Logger) LoggerFactory.getLogger(Cleaner.class);
    private final long oldThread = Instant.now().toEpochMilli()- Duration.ofMinutes(30).toMillis();
    private final long oldBoard = Instant.now().toEpochMilli()-Duration.ofDays(1).toMillis();
    private final String bucket = "images";
    private final MinioService minioService;

    @Scheduled(cron = "0 0 4 * * WED")
    public void cleanThreads(){

        ArrayList<Threads> deletedThreads = cleanerRepository.threads(oldThread);

        deletedThreads.forEach( thread -> minioService.deletePicture(thread.getPicture()));

        logger.info("{} threads deleted in {}.", deletedThreads.size(), LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 4 * * MON")
    public void cleanPosts(){

        ArrayList<Posts> deletedPosts = cleanerRepository.posts();

        deletedPosts.forEach(post->{
            String url = post.getPicture();
            if (!url.isEmpty())
                minioService.deletePicture(url);
        });

        logger.info("{} posts deleted in {}.", deletedPosts.size(), LocalDateTime.now());
    }

    @Scheduled(cron ="0 0 4 13 * *")
    public void cleanPics(){

        ArrayList<String> pics = cleanerRepository.pics();
        ArrayList<String> realPics = minioService.getAllFileName(bucket);

        realPics.forEach( pic -> {
            if (!pics.contains(pic))
                minioService.deletePicture(pic);
        });
    }

    @Scheduled(cron="0 0 5 24 * *")
    public void cleanBoards(){

        ArrayList<Board> deletedBoards = cleanerRepository.boards(oldBoard);

        deletedBoards.forEach(board ->
            logger.info("{} board was deleted. This board has a {} users", board.getName(), board.getUsers().size())
        );

        logger.info("{} boards deleted in {}.", deletedBoards.size(), LocalDateTime.now());

    }
}
