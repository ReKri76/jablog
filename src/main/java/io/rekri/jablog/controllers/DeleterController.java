package io.rekri.jablog.controllers;

import io.rekri.jablog.service.DeleterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Эндпоинты с правами доступа к удалению
 * */
@RestController
@RequestMapping("/api/deleter")
@RequiredArgsConstructor
public class DeleterController {

    private final DeleterService deleterService;

    @DeleteMapping(value = "/{boardName}/{threadId}")
    public ResponseEntity<Void> thread(@PathVariable(value = "boardName") String boardName,
                                       @PathVariable("threadId") long threadId){
        deleterService.thread(threadId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{boardName}/{threadId}/{postId}")
    public ResponseEntity<Void> post(@PathVariable("boardName") String boardName, @PathVariable("postId") long postId,
                                      @PathVariable("threadId") long threadId){

        deleterService.post(postId);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{boardName}/{threadId}")
    public ResponseEntity<Void> canDeleteThreads(@PathVariable(value = "boardName") String boardName,
                                       @PathVariable("threadId") long threadId){
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{boardName}/{threadId}/{postId}")
    public ResponseEntity<Void> canDeletePosts(@PathVariable("boardName") String boardName,
                                               @PathVariable("postId") long postId, @PathVariable("threadId") long threadId){
        return ResponseEntity.ok().build();
    }
}
