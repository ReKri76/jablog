package com.example.jablog.service;

import com.example.jablog.repository.DeleterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleterService {

    private final DeleterRepository deleterRepository;

    @Transactional
    public void thread(long threadId, String boardName){
        deleterRepository.thread(threadId, boardName);
    }

    @Transactional
    public void post(long postId, String boardName){
        deleterRepository.post(postId, boardName);
    }
}
