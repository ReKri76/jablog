package com.example.jablog.controllers;

import com.example.jablog.DTO.Post;
import com.example.jablog.DTO.PostWithPicture;
import com.example.jablog.service.GetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
public class Getter {

    private final GetterService getterService;

    @GetMapping("/{boardName}/{threadId}")
    public String thread(@PathVariable long threadId, @PathVariable String boardName, Model model){

        ArrayList<PostWithPicture> posts = getterService.thread(threadId, boardName);

        model.addAttribute("thread", posts.getFirst());
        posts.removeFirst();
        model.addAttribute("posts", posts);
        model.addAttribute("boardName", boardName);
        model.addAttribute("post", new Post());
        return "thread";
    }

    @GetMapping("/{boardName}")
    public String board(@PathVariable String boardName, @RequestParam(defaultValue = "0") int page, Model model){

        ArrayList<PostWithPicture> threads = getterService.board(boardName, page);
        model.addAttribute("threads", threads);
        model.addAttribute("boardName", boardName);
        model.addAttribute("post", new Post());
        return "board";
    }

    @GetMapping("/")
    public String start(Model model){

        ArrayList<String> boards = getterService.start();
        model.addAttribute("boards", boards);
        return "index";
    }
}
