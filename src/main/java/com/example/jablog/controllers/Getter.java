package com.example.jablog.controllers;

import com.example.jablog.DTO.PostWithPicture;
import com.example.jablog.service.GetterService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
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
    public String board(@PathVariable String boardName, @RequestParam(defaultValue = "0") int page, @NonNull Model model){

        LinkedList<PostWithPicture> threads = getterService.board(boardName, page);
        model.addAttribute("threads", threads);
        return "board";
    }

    @GetMapping("/{threadId}")
    public String thread(@PathVariable long threadId, @NonNull Model model){

        LinkedList<PostWithPicture> posts = getterService.thread(threadId);
        model.addAttribute("posts", posts);
        return "thread";
    }

    @GetMapping("/")
    public String start(@NonNull Model model){

        LinkedList<String> boards = getterService.start();
        model.addAttribute("boards", boards);
        return "index";
    }
}
