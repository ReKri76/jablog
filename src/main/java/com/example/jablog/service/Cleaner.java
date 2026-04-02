package com.example.jablog.service;

import ch.qos.logback.classic.Logger;
import com.example.jablog.DTO.CleanPostRecord;
import com.example.jablog.repository.Deleter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class Cleaner {

    private final Deleter deleter;
    private final Logger logger;
    private final long delta = Duration.ofDays(7).getSeconds();

    @Scheduled(cron = "0 0 0 * * TUR")
    public void cleanThreads(){

    }

    @Scheduled(cron = "0 0 0 * * MON")
    public void cleanPosts(){
        CleanPostRecord number = deleter.posts(delta);
        logger.info("{} posts deleted in {}.", number, LocalDateTime.now());
    }

    //добавить ежемесячный цикл для чистки minio
}
