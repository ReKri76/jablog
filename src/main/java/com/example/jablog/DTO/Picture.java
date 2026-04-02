package com.example.jablog.DTO;

import lombok.Data;

import java.io.InputStream;

@Data
public class Picture {

    private InputStream inputStream;
    private long size;
    private String name;
    private String contentType;

}
