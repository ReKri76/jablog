package com.example.jablog.controllers;

import com.example.jablog.service.DeleterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deleter")
@RequiredArgsConstructor
public class Deleter {

    private final DeleterService deleterService;

    @DeleteMapping(value = "/{boardName}/{threadId}")
    public ResponseEntity<Void> thread(@PathVariable("boardName") String boardName,
                                       @PathVariable("threadId") long threadId){

        deleterService.thread(threadId);

        return ResponseEntity
                .ok()
                .header("HX-Redirect", "/"+boardName)
                .build();
    }

    @DeleteMapping(value = "/{boardName}/{threadId}/{postId}")
    public  ResponseEntity<Void> post(@PathVariable("boardName") String boardName, @PathVariable("postId") long postId,
                                      @PathVariable("threadId") long threadId){

        deleterService.post(postId);

        return ResponseEntity
                .ok()
                .header("HX-Redirect", "/"+boardName+"/"+threadId)
                .build();
    }

}
