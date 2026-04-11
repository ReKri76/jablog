package com.example.jablog.controllers;

import com.example.jablog.service.DeleterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/deleter")
@RequiredArgsConstructor
public class Deleter {

    private final DeleterService deleterService;

    @DeleteMapping(value = "/post")
    public String post(){
        return "error-page";
    }

}
