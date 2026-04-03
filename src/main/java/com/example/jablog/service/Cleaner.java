package com.example.jablog.service;

import ch.qos.logback.classic.Logger;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.repository.Deleter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;

@Service
@RequiredArgsConstructor
public class Cleaner {

    private final Deleter deleter;
    private final static Logger logger = (Logger) LoggerFactory.getLogger(Cleaner.class);
    private final long deltaPost = Duration.ofDays(14).toMillis();
    private final long deltaThread = Duration.ofDays(7).toMillis();
    private final MinioService minioService;

    @Scheduled(cron = "0 0 4 * * WED")
    @Transactional
    public void cleanThreads(){
        ArrayDeque<Threads> deletedThreads = deleter.threads(deltaThread);

        deletedThreads.forEach( thread -> minioService.deletePicture(thread.getPicture()));

        logger.info("{} threads deleted in {}.", deletedThreads.size(), LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 4 * * MON")
    @Transactional
    public void cleanPosts(){
        ArrayDeque<Posts> deletedPosts = deleter.posts(deltaPost);

        deletedPosts.forEach(post->{
            String url = post.getPicture();
            if (!url.isEmpty())
                minioService.deletePicture(url);
        });

        logger.info("{} posts deleted in {}.", deletedPosts.size(), LocalDateTime.now());
    }

    //добавить ежемесячный цикл для чистки minio
}
