package com.example.jablog.controllers;

import com.example.jablog.DTO.Picture;
import com.example.jablog.DTO.Post;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.jablog.service.PosterService;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Controller
@RequestMapping("/poster")
@RequiredArgsConstructor
public class Poster {

    final public static long maxImageSize = 1024*1024*10-1;

    private final PosterService posterService;

    @PostMapping(value = "/{boardName}", consumes = "multipart/form-data")
    public String thread(@PathVariable String boardName, @Valid @ModelAttribute("post") Post post,
                         @RequestPart("image") @NonNull MultipartFile file) throws IOException {

        chekFile(file);

        if (file.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dosent upload image");

        Picture picture = new Picture(file);
        long ifOfPost = posterService.thread(post, picture, boardName);

        return "redirect:/"+boardName+"/"+ifOfPost;
    }

    @PostMapping(value = "/{boardName}/{threadID}", consumes = "multipart/form-data")
    public String post(@PathVariable String boardName, @PathVariable long threadID, @Valid @ModelAttribute("post") Post post,
                       @RequestPart(value = "image", required = false) MultipartFile file) throws IOException {

        Picture picture = null;
        if (file != null && !file.isEmpty()) {
            chekFile(file);
            picture = new Picture(file);
        }

        posterService.post(post, picture, threadID);

        return "redirect:/"+boardName+"/"+threadID;
    }

    @PostMapping(value = "/board")
    public String board(@RequestParam("boardName") @NonNull String boardName, @RequestParam("rule") @NonNull String rule,
                        @RequestParam("pass") @NonNull String pass, @RequestParam("nickname") @NonNull String nickname,
                        @RequestParam("lifeCycleThreads") int lifeCycleThreads,
                        @RequestParam("lifeCyclePosts") int lifeCyclePosts){

        posterService.board(boardName, pass, rule, nickname, lifeCycleThreads, lifeCyclePosts);

        //TODO: выдать сессию

        return "redirect:/"+boardName;
    }

    @PostMapping(value = "/user")
    public String users(@RequestParam("boardName") @NonNull String boardName,
                        @RequestParam("pass") @NonNull String pass, @RequestParam("nickname") @NonNull String nickname){

        posterService.user(boardName, nickname, pass);

        //TODO: выдать сессию
        //TODO: авторизация

        return "redirect:/"+boardName;
    }



    private void chekFile(@NonNull MultipartFile file){
        String fileType = file.getContentType();
        if (file.getSize()>maxImageSize)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file is too big");
        if (!fileType.equals("image/png") && !fileType.equals("image/jpeg")
                && !fileType.equals("image/jpg") && !fileType.equals("image/gif"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file is not image");
    }
}
