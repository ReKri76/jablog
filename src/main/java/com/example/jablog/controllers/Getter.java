package com.example.jablog.controllers;

import com.example.jablog.DTO.Board;
import com.example.jablog.DTO.Post;
import com.example.jablog.DTO.PostWithPicture;
import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.service.GetterService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
public class Getter {

    private final GetterService getterService;

    @GetMapping("/{boardName:[^.\\/]+}/{threadId:\\d+}")
    public String thread(@PathVariable long threadId, @PathVariable String boardName, Model model, HttpSession session){

        final ArrayList<PostWithPicture> posts = getterService.thread(threadId);
        final CustomUserDetails customUserDetails = (CustomUserDetails) session.getAttribute(boardName);

        PostWithPicture thread =  posts.getFirst();

        model.addAttribute("thread",thread);
        posts.removeFirst();
        model.addAttribute("posts", posts);
        model.addAttribute("boardName", boardName);
        model.addAttribute("post", new Post());
        model.addAttribute("canDelete", getterService.canDelete(boardName, customUserDetails,
                Long.toString(thread.getId())));
        return "thread";
    }

    @GetMapping("/{boardName:[^.\\/]+}")
    public String board(@PathVariable String boardName, @RequestParam(defaultValue = "0") int page, Model model,
                        HttpSession session){

        final ArrayList<PostWithPicture> threads = getterService.board(boardName, page);
        final CustomUserDetails customUserDetails = (CustomUserDetails) session.getAttribute(boardName);

        model.addAttribute("threads", threads);
        model.addAttribute("boardName", boardName);
        model.addAttribute("post", new Post());
        model.addAttribute("canDelete", getterService.canDelete(boardName, customUserDetails, null));
        return "board";
    }

    @GetMapping("/")
    public String start(Model model){

        final ArrayList<Board> boards = getterService.start();
        model.addAttribute("boards", boards);
        return "index";
    }
}
