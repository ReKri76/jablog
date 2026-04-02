package com.example.jablog.service;

import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.repository.GetterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class GetterService {

    private final GetterRepository getterRepository;

    @Transactional
    public LinkedList<Board> start(){
        return getterRepository.start();
    }

    @Transactional
    public LinkedList<Threads> board(String boardName, int page){
        return getterRepository.board(boardName, page);
    }

    @Transactional
    public LinkedList<Posts> thread(long threadId){
        return getterRepository.thread(threadId);
    }
}
