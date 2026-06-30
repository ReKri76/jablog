package com.example.jablog.DTO;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Data
public class Picture {

    public Picture(@NotNull MultipartFile file) throws IOException {
        this.setInputStream(file.getInputStream());
        this.setName("["+file.getOriginalFilename()+"]["+System.currentTimeMillis()+"]");
        this.setSize(file.getSize());
        this.setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
    }

    @NotNull
    private InputStream inputStream;
    private long size;
    @NotNull
    private String name;
    @NotNull
    private String contentType;
}
