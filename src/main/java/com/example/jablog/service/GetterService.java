package com.example.jablog.service;

import com.example.jablog.entity.Board;
import com.example.jablog.repository.GetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class GetterService {

    GetterRepository getterRepository;

    public LinkedList<Board> start(){
        return getterRepository.start();
    }
}
