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

        ArrayList<PostWithPicture> posts = new ArrayList<PostWithPicture>();
        ArrayList<Posts> input = getterRepository.thread(threadId, boardName);

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

    @Transactional
    public PostWithPicture getThread(long threadId){
        Threads thread = getterRepository.getThread(threadId);

        PostWithPicture postWithPicture = new PostWithPicture();
        postWithPicture.setId(thread.getId());
        postWithPicture.setUrl(thread.getPicture());
        postWithPicture.setHead(thread.getHeader());
        postWithPicture.setBody(thread.getContent());

        return postWithPicture;
    }
}
