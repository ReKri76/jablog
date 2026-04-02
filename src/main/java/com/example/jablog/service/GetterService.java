package com.example.jablog.service;

import com.example.jablog.DTO.PostWithPicture;
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
    public LinkedList<String> start(){

        LinkedList<String> boards = new LinkedList<String>();
        LinkedList<Board> input = getterRepository.start();

        input.forEach(board -> boards.add(board.getName()));

        return boards;
    }

    @Transactional
    public LinkedList<PostWithPicture> board(String boardName, int page){

        LinkedList<PostWithPicture> threads = new LinkedList<PostWithPicture>();
        LinkedList<Threads> input = getterRepository.board(boardName, page);

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
    public LinkedList<PostWithPicture> thread(long threadId){

        LinkedList<PostWithPicture> posts = new LinkedList<PostWithPicture>();
        LinkedList<Posts> input = getterRepository.thread(threadId);

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
