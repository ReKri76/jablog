package com.example.jablog.service;

import com.example.jablog.repository.DeleterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleterService {

    private final DeleterRepository deleterRepository;

    @Transactional
    public void thread(long threadId){
        log.info("Thread number {} is start deleting.",threadId);
        deleterRepository.thread(threadId);
        log.info("Thread number {} is deleted.",threadId);
    }

    @Transactional
    public void post(long postId){
        log.info("Post number {} is start deleting.",postId);
        deleterRepository.post(postId);
        log.info("Post number {} is deleted.",postId);
    }
}
