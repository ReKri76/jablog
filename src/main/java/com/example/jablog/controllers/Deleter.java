package com.example.jablog.controllers;

import com.example.jablog.service.DeleterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/deleter")
@RequiredArgsConstructor
public class Deleter {

    private final DeleterService deleterService;

    @DeleteMapping(value = "/{boardName}/{threadId}")
    public String thread(@PathVariable("boardName") String boardName, @PathVariable("threadId") long threadId){

        deleterService.thread(threadId);

        return "redirect:/"+boardName;
    }

    @DeleteMapping(value = "/{boardName}/{postId}")
    public String post(@PathVariable("boardName") String boardName, @PathVariable("postId") long postId){

        deleterService.post(postId);

        return "redirect:/"+boardName;
    }

}
