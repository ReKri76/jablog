package com.example.jablog.service;

import com.example.jablog.DTO.PostWithPicture;
import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.repository.GetterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class GetterService {

    private final GetterRepository getterRepository;
    private final SecurityAccessService securityAccessService;
    private final CustomUserDetailsService customUserDetailsService;

    @Transactional
    public ArrayList<String> start(){

        final ArrayList<String> boards = new ArrayList<String>();
        final ArrayList<Board> input = getterRepository.start();

        input.forEach(board -> boards.add(board.getName()));

        return boards;
    }

    @Transactional
    public ArrayList<PostWithPicture> board(String boardName, int page){

        final ArrayList<PostWithPicture> threads = new ArrayList<PostWithPicture>();
        final ArrayList<Threads> input = getterRepository.board(boardName, page);

        input.forEach(thread -> {

            final PostWithPicture postWithPicture= new PostWithPicture();
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

        final Threads threads = getterRepository.thread(threadId, boardName);

        final PostWithPicture main = new PostWithPicture();
        main.setId(threads.getId());
        main.setUrl(threads.getPicture());
        main.setHead(threads.getHeader());
        main.setBody(threads.getContent());

        final ArrayList<PostWithPicture> posts = new ArrayList<PostWithPicture>();
        posts.add(main);

        final TreeSet<Posts> input = new TreeSet<Posts>(threads.getPosts());
        input.forEach(post -> {

            final PostWithPicture postWithPicture= new PostWithPicture();
            postWithPicture.setId(post.getId());
            postWithPicture.setUrl(post.getPicture());
            postWithPicture.setHead(post.getHeader());
            postWithPicture.setBody(createAnchor(post.getContent()));

            posts.add(postWithPicture);
        });

        return posts;
    }

    @Transactional
    public boolean canDelete(String boardName, CustomUserDetails customUserDetails, boolean isThread){

        if (customUserDetails == null)
            customUserDetails = customUserDetailsService.createDefault();

        return securityAccessService.canAccess(boardName, customUserDetails, "DELETE", isThread, false);
    }

    private String createAnchor(String text){
        text = HtmlUtils.htmlEscape(text);

        text = text.replaceAll("&gt;&gt;(\\d+)", "<a class=\"anchor\" href=\"#p$1\">&gt;&gt;$1</a>");

        return text;
    }

}
