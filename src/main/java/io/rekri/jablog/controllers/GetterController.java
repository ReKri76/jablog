package io.rekri.jablog.controllers;

import io.rekri.jablog.DTO.Board;
import io.rekri.jablog.DTO.PostWithPicture;
import io.rekri.jablog.DTO.SimpleResponse;
import io.rekri.jablog.service.GetterService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GetterController {

    private final GetterService getterService;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ThreadResponse extends SimpleResponse{
        private PostWithPicture thread;
        private List<PostWithPicture> posts;
        private String boardName;
    }

    @GetMapping("/{boardName:[^.\\/]+}/{threadId:\\d+}")
    public ResponseEntity<ThreadResponse> thread(@PathVariable long threadId, @PathVariable String boardName){

        final ThreadResponse res = new ThreadResponse();

        final List<PostWithPicture> posts = getterService.thread(threadId);

        final PostWithPicture thread =  posts.getFirst();

        res.setThread(thread);
        posts.removeFirst();
        res.setPosts(posts);
        res.setBoardName(boardName);

        res.setStatus(200);
        res.setMessage("ok");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class BoardResponse extends SimpleResponse{
        private List<PostWithPicture> threads;
        private String boardName;
    }

    @GetMapping("/{boardName:[^.\\/]+}")
    public ResponseEntity<BoardResponse> board(@PathVariable String boardName, @RequestParam(defaultValue = "0") int page){

        final BoardResponse res = new BoardResponse();

        final List<PostWithPicture> threads = getterService.board(boardName, page);

        res.setThreads(threads);
        res.setBoardName(boardName);

        res.setMessage("ok");
        res.setStatus(200);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @GetMapping("/{boardName}/img/{fileName}")
    @ResponseBody
    @NotNull
    public StreamingResponseBody file(@PathVariable String fileName){
        return getterService.file(fileName);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class RootResponse extends SimpleResponse{
        private List<Board> boards;
    }

    @GetMapping("/")
    public ResponseEntity<RootResponse> start(){

        final RootResponse res = new RootResponse();

        final List<Board> boards = getterService.start();
        res.setBoards(boards);
        res.setStatus(200);
        res.setMessage("ok");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @PatchMapping("/carma/plus/{boardName}/{threadId}")
    public ResponseEntity<Void> likeThread(@PathVariable int threadId){
        getterService.likeThread(threadId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/carma/minus/{boardName}/{threadId}")
    public ResponseEntity<Void> dislikeThread(@PathVariable int threadId){
        getterService.dislikeThread(threadId);
        return ResponseEntity.ok().build();
    }
}
