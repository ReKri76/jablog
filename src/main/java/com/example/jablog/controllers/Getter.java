package com.example.jablog.controllers;

import com.example.jablog.DTO.PostWithPicture;
import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.service.GetterService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/getter")
@RequiredArgsConstructor
public class Getter {

    private final GetterService getterService;

    public record Getter_boardName_threadID(
            PostWithPicture thread,
            ArrayList<PostWithPicture> posts,
            String boardName,
            boolean canDelete
    ){}

    @GetMapping("/{boardName}/{threadId}")
    public ResponseEntity<Getter_boardName_threadID> thread(@PathVariable long threadId, @PathVariable String boardName, HttpSession session){

        final ArrayList<PostWithPicture> posts = getterService.thread(threadId);
        final CustomUserDetails customUserDetails = (CustomUserDetails) session.getAttribute(boardName);

        PostWithPicture thread =  posts.getFirst();
        posts.removeFirst();

        Getter_boardName_threadID record = new Getter_boardName_threadID(thread,
                posts,
                boardName,
                getterService.canDelete(boardName, customUserDetails, Long.toString(thread.getId())));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(record);
    }

    public record Getter_boardName(
            ArrayList<PostWithPicture> threads,
            String boardName,
            boolean canDelete
    ){}

    @GetMapping("/{boardName}")
    public ResponseEntity<Getter_boardName> board(@PathVariable String boardName, @RequestParam(defaultValue = "0") int page,
                        HttpSession session){

        final ArrayList<PostWithPicture> threads = getterService.board(boardName, page);
        final CustomUserDetails customUserDetails = (CustomUserDetails) session.getAttribute(boardName);

        Getter_boardName record = new Getter_boardName(
                threads,
                boardName,
                getterService.canDelete(boardName, customUserDetails, Long.toString(threads.getFirst().getId()))
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(record);
    }

    public record Getter_(
            ArrayList<String> boards
    ){}

    @GetMapping("/")
    public ResponseEntity<Getter_> start(){

        final ArrayList<String> boards = getterService.start();
        Getter_ record = new Getter_(boards);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(record);
    }
}
