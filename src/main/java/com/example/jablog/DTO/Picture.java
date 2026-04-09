package com.example.jablog.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Data
public class Picture {

    public Picture(MultipartFile file) throws IOException {
        this.setInputStream(file.getInputStream());
        this.setName("["+file.getOriginalFilename()+"]["+System.currentTimeMillis()+"]");
        this.setSize(file.getSize());
        this.setContentType(file.getContentType());
    }

    private InputStream inputStream;
    private long size;
    private String name;
    private String contentType;

}
