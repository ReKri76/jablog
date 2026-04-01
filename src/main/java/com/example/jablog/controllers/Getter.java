package com.example.jablog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class Getter {

    @GetMapping("/{boardName}")
    public String board(@PathVariable String boardName){


    }

    @GetMapping("/{bordName}/{threadId}")
    public String thread(@PathVariable String boardName, @PathVariable String threadId){


    }

    @GetMapping("/")
    public String start(){

        
    }

}
