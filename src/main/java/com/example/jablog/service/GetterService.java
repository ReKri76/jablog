package com.example.jablog.service;

import com.example.jablog.DTO.PostWithPicture;
import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.repository.GetterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class GetterService {

    private final GetterRepository getterRepository;

    @Transactional
    public ArrayList<String> start(){

        ArrayList<String> boards = new ArrayList<String>();
        ArrayList<Board> input = getterRepository.start();

        input.forEach(board -> boards.add(board.getName()));

        return boards;
    }

    @Transactional
    public ArrayList<PostWithPicture> board(String boardName, int page){

        ArrayList<PostWithPicture> threads = new ArrayList<PostWithPicture>();
        ArrayList<Threads> input = getterRepository.board(boardName, page);

        input.forEach(thread -> {

            PostWithPicture postWithPicture= new PostWithPicture();
            postWithPicture.setId(thread.getId());
            postWithPicture.setUrl(thread.getPicture());
            postWithPicture.setHead(thread.getHeader());
            postWithPicture.setBody(thread.getContent());

            threads.add(postWithPicture);
        });

        return threads;
    }

    @Transactional
    public ArrayList<PostWithPicture> thread(long threadId, String boardName){

        Threads threads = getterRepository.thread(threadId, boardName);

        PostWithPicture main= new PostWithPicture();
        main.setId(threads.getId());
        main.setUrl(threads.getPicture());
        main.setHead(threads.getHeader());
        main.setBody(threads.getContent());

        ArrayList<PostWithPicture> posts = new ArrayList<PostWithPicture>();
        posts.add(main);

        HashSet<Posts> input = new HashSet<Posts>(threads.getPosts());
        input.forEach(post -> {

            PostWithPicture postWithPicture= new PostWithPicture();
            postWithPicture.setId(post.getId());
            postWithPicture.setUrl(post.getPicture());
            postWithPicture.setHead(post.getHeader());
            postWithPicture.setBody(post.getContent());

            posts.add(postWithPicture);
        });

        return posts;
    }
}
