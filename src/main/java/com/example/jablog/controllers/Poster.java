package com.example.jablog.controllers;

import com.example.jablog.DTO.Picture;
import com.example.jablog.DTO.Post;
import com.example.jablog.service.PosterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/poster")
@RequiredArgsConstructor
public class Poster {

    public static final long MAX_IMAGE_SIZE = 1024 * 1024 * 10 - 1;

    private final PosterService posterService;

    @PostMapping(value = "/{boardName}", consumes = "multipart/form-data")
    public ResponseEntity<String> thread(@PathVariable String boardName, @Valid @ModelAttribute("post") Post post,
                                                 @RequestPart("image") @NonNull MultipartFile file) throws IOException {

        chekFile(file);

        if (file.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dosent upload image");

        final Picture picture = new Picture(file);
        final long idOfPost = posterService.thread(post, picture, boardName);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", boardName + "/" + idOfPost)
                .body(boardName+"/"+idOfPost);
    }

    @PostMapping(value = "/{boardName}/{threadID}", consumes = "multipart/form-data")
    public ResponseEntity<String> post(@PathVariable String boardName, @PathVariable long threadID, @Valid @ModelAttribute("post") Post post,
                                               @RequestPart(value = "image", required = false) MultipartFile file) throws IOException {

        Picture picture = null;
        if (file != null && !file.isEmpty()) {
            chekFile(file);
            picture = new Picture(file);
        }

        posterService.post(post, picture, threadID);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", boardName + "/" + threadID)
                .body(boardName+"/"+threadID);
    }

    @PostMapping(value = "/board")
    public ResponseEntity<String> board(
            @RequestParam("boardName") @NonNull String boardName,
            @RequestParam("rule") @NonNull String rule,
            @RequestParam("pass") @NonNull String pass,
            @RequestParam("nickname") @NonNull String nickname,
            @RequestParam("lifeCycleThreads") int lifeCycleThreads,
            @RequestParam("lifeCyclePosts") int lifeCyclePosts){

        posterService.board(boardName, pass, rule, nickname, lifeCycleThreads, lifeCyclePosts);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", boardName)
                .body(boardName);
    }

    private void chekFile(@NonNull MultipartFile file){
        final String fileType = file.getContentType();
        if (file.getSize() > MAX_IMAGE_SIZE)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file is too big");
        if (!fileType.equals("image/png") && !fileType.equals("image/jpeg")
                && !fileType.equals("image/jpg") && !fileType.equals("image/gif"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file is not image");
    }
}
