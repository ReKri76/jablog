package io.rekri.jablog.controllers;

import io.rekri.jablog.DTO.BoardToCreate;
import io.rekri.jablog.DTO.Picture;
import io.rekri.jablog.DTO.Post;
import io.rekri.jablog.service.CustomUserDetailsService;
import io.rekri.jablog.service.PosterService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/poster")
@RequiredArgsConstructor
public class PosterController {

    public static final long MAX_IMAGE_SIZE = 1024 * 1024 * 10 - 1;

    private final PosterService posterService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping(value = "/{boardName}", consumes = "multipart/form-data")
    public ResponseEntity<Void> thread(@PathVariable String boardName, @Valid @RequestPart("post") Post post,
                                 @RequestPart("image") @NonNull MultipartFile file) throws IOException {

        if (file.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dosent upload image");

        chekFile(file);

        final Picture picture = new Picture(file);
        final long idOfPost = posterService.thread(post, picture, boardName);

        return ResponseEntity.created(URI.create("/"+boardName+"/"+idOfPost)) .build();
    }

    @PostMapping(value = "/{boardName}/{threadID}", consumes = "multipart/form-data")
    public ResponseEntity<Void> post(@PathVariable String boardName, @PathVariable long threadID, @Valid @RequestPart("post") Post post,
                       @RequestPart(value = "image", required = false) @Nullable MultipartFile file) throws IOException {

        Picture picture = null;
        if (file != null && !file.isEmpty()) {
            chekFile(file);
            picture = new Picture(file);
        }

        posterService.post(post, picture, threadID);

        return ResponseEntity.created(URI.create("/"+boardName+"/"+threadID)).build();
    }

    @PostMapping(value = "/board")
    public ResponseEntity<Void> board(@Valid @RequestBody BoardToCreate board, HttpSession session){

        posterService.board(board);

        session.setAttribute(board.getBoardName(), customUserDetailsService.loadUserByUsername(board.getNickname()));
        return ResponseEntity.created(URI.create("/"+board.getBoardName())).build();
    }

    private void chekFile(@NonNull MultipartFile file){
        final String fileType = file.getContentType();
        if (file.getSize() > MAX_IMAGE_SIZE)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file is too big");
        if (!fileType.equals("image/png") && !fileType.equals("image/jpeg")
                && !fileType.equals("image/jpg") && !fileType.equals("image/gif") && !fileType.equals("image/webp"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file is not image");
    }
}
