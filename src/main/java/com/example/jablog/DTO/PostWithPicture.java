package com.example.jablog.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostWithPicture extends Post{
    private String url;
    private long id;
}
