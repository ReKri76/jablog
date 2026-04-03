package com.example.jablog.service;

import com.example.jablog.DTO.PostWithPicture;
import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.repository.GetterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;

@Service
@RequiredArgsConstructor
public class GetterService {

    private final GetterRepository getterRepository;

    @Transactional
    public ArrayDeque<String> start(){

        ArrayDeque<String> boards = new ArrayDeque<String>();
        ArrayDeque<Board> input = getterRepository.start();

        input.forEach(board -> boards.add(board.getName()));

        return boards;
    }

    @Transactional
    public ArrayDeque<PostWithPicture> board(String boardName, int page){

        ArrayDeque<PostWithPicture> threads = new ArrayDeque<PostWithPicture>();
        ArrayDeque<Threads> input = getterRepository.board(boardName, page);

        input.forEach(thread -> {

            PostWithPicture postWithPicture= new PostWithPicture();
            postWithPicture.setUrl(thread.getPicture());
            postWithPicture.setHead(thread.getHeader());
            postWithPicture.setBody(thread.getContent());

            threads.add(postWithPicture);
        });

        return threads;
    }

    @Transactional
    public ArrayDeque<PostWithPicture> thread(long threadId){

        ArrayDeque<PostWithPicture> posts = new ArrayDeque<PostWithPicture>();
        ArrayDeque<Posts> input = getterRepository.thread(threadId);

        input.forEach(post -> {

            PostWithPicture postWithPicture= new PostWithPicture();
            postWithPicture.setUrl(post.getPicture());
            postWithPicture.setHead(post.getHeader());
            postWithPicture.setBody(post.getContent());

            posts.add(postWithPicture);
        });

        return posts;
    }
}
