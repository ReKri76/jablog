package com.example.jablog.service;

import com.example.jablog.controllers.Poster;
import com.example.jablog.entity.Board;
import com.example.jablog.entity.Threads;
import com.example.jablog.repository.PosterRepository;
import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PosterService {

    @Autowired
    PosterRepository posterRepository;

    private final Board board = new Board();

    public long thread(Poster.@NonNull Post post, MultipartFile file, String board){

        if (post.getHead().isEmpty())
            post.setHead(post.getBody().substring(0,126));

        this.board.setName(board);

        Threads threads = new Threads();
        threads.setContent(post.getBody());
        threads.setHeader(post.getHead());
        threads.setBoard(this.board);

        long idOfThread = posterRepository.save(threads, file);

        return idOfThread;
    }
}
