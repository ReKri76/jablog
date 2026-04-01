package com.example.jablog.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Post {

    @Size(max = 120, message = "head too long")
    private String head;

    @NotBlank
    @Size(max=4095, message = "body too long")
    private String body;

}