package io.rekri.jablog.controllers;

import io.rekri.jablog.DTO.Board;
import io.rekri.jablog.DTO.Post;
import io.rekri.jablog.DTO.PostWithPicture;
import io.rekri.jablog.config.security.CustomUserDetails;
import io.rekri.jablog.service.GetterService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/{boardName}/img/{fileName}")
    @ResponseBody
    @NotNull
    public StreamingResponseBody file(@PathVariable String fileName){
        return getterService.file(fileName);
    }

    @GetMapping("/")
    public String start(Model model){

        final List<Board> boards = getterService.start();
        model.addAttribute("boards", boards);
        return "index";
    }

    @PatchMapping("/carma/plus/{boardName}/{threadId}")
    public void likeThread(@PathVariable int threadId){
        getterService.likeThread(threadId);
    }

    @PatchMapping("/carma/minus/{boardName}/{threadId}")
    public void dislikeThread(@PathVariable int threadId){
        getterService.dislikeThread(threadId);
    }
}
