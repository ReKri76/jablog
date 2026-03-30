package com.example.jablog.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.jablog.service.PosterService;
import org.springframework.web.server.ResponseStatusException;


@Controller
@RequestMapping("/poster")
public class Poster {

    final long maxImageSize = 1024*1024*10-1;

    @Setter
    @Getter
    public static class Post {

        @Size(max = 127, message = "head too long")
        private String head;

        @NotBlank
        @Size(max=4095, message = "body too long")
        private String body;

    }

    @Autowired
    private PosterService posterService;

    @PostMapping(value = "/{boardName}/", consumes = "multipart/form-data")
    public String thread(@PathVariable String boardName, @Valid @RequestPart("post") Post post, @RequestPart("image") @NonNull MultipartFile file){
        chekFile(file);
        long ifOfPost = posterService.thread(post, file, boardName);

        return "redirect:/"+boardName+"/"+ifOfPost;
    }

    @PostMapping(value = "/{boardName}/{threadID}/", consumes = "multipart/form-data")
    public String post(@PathVariable String boardName, @PathVariable int threadID,
                       @Valid @RequestPart("post") Post post, @RequestPart(value = "image", required = false) MultipartFile file){
        chekFile(file);

        int ifOfPost = posterService.post(post, file, boardName, threadID);

        return "redirect:/"+boardName+"/"+ifOfPost;
    }

    private void chekFile(MultipartFile file){
        String fileType = file.getContentType();
        if (file.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dosent upload image");
        if (file.getSize()>maxImageSize)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file is too big");
        if (!fileType.equals("image/png") && !fileType.equals("image/jpeg") && !fileType.equals("image/jpg") && !fileType.equals("image/gif"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file is not image");
    }

}
