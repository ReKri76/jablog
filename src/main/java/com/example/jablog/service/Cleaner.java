package com.example.jablog.service;

import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.repository.CleanerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class Cleaner {

    private final CleanerRepository cleanerRepository;
    private final MinioService minioService;

    @Scheduled(cron = "0 0 4 * * WED")
    public void cleanThreads(){

        log.info("start to clean threads");

        final long oldThread = Instant.now().toEpochMilli()- Duration.ofMinutes(30).toMillis();

        final List<Threads> deletedThreads = cleanerRepository.threads(oldThread);

        deletedThreads.forEach( thread -> minioService.deletePicture(thread.getPicture()));

        log.info("{} threads deleted.", deletedThreads.size());
    }

    @Scheduled(cron = "0 0 4 * * MON")
    public void cleanPosts(){

        log.info("start to clean posts");

        final List<Posts> deletedPosts = cleanerRepository.posts();

        deletedPosts.forEach(post->{
            final String url = post.getPicture();
            if (!url.isEmpty())
                minioService.deletePicture(url);
        });

        log.info("{} posts deleted in.", deletedPosts.size());
    }

    @Scheduled(cron ="0 0 4 13 * *")
    public void cleanPics(){

        log.info("start to clean pictures");

        final List<String> picsInDB = cleanerRepository.pics();
        final HashSet<String> picsSet = new HashSet<>(picsInDB);
        final ArrayList<String> picsInS3 = minioService.getAllFileName(MinioService.BUCKET);
        AtomicInteger count = new AtomicInteger();

        picsInS3.forEach( pic -> {
            if (!picsSet.contains(pic)){
                minioService.deletePicture(pic);
                count.incrementAndGet();
            }
        });

        log.info("{} pictures was deleted.", count.get());

    }

    @Scheduled(cron="0 0 5 24 * *")
    public void cleanBoards(){

        log.info("start to clean boards");

        final long oldBoard = Instant.now().toEpochMilli()-Duration.ofDays(1).toMillis();

        final List<Board> deletedBoards = cleanerRepository.boards(oldBoard);

        deletedBoards.forEach(board ->
                log.info("{} board was deleted. This board has a {} users", board.getName(), board.getUsers().size())
        );

        log.info("{} boards deleted.", deletedBoards.size());

    }
}
