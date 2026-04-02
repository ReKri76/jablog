package com.example.jablog.controllers;

import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.service.GetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedList;

@Controller
@RequiredArgsConstructor
public class Getter {

    private final GetterService getterService;

    @GetMapping("/{boardName}")
    public String board(@PathVariable String boardName, @RequestParam(defaultValue = "0") int page, Model model){

        LinkedList<Threads> threads = getterService.board(boardName, page);

        threads.forEach(thread ->
                model.addAttribute(Long.toString(thread.getId()), thread)
        );

        return "board";
    }

    @GetMapping("/{threadId}")
    public String thread(@PathVariable long threadId, Model model){

        LinkedList<Posts> posts = getterService.thread(threadId);

        posts.forEach(post ->
                model.addAttribute(Long.toString(post.getId()), post)
        );

        return "thread";
    }

    @GetMapping("/")
    public String start(Model model){

        LinkedList<Board> boards = getterService.start();

        boards.forEach(board->
            model.addAttribute(board.getName(), board)
        );

        return "index";
    }
}
