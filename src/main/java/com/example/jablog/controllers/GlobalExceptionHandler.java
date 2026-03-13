package com.example.jablog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@ControllerAdvice
public class GlobalExceptionHandler {

    @RequestMapping("/error-page")
    public String getErrorPage(){
        return "error-page";
    }

    @ExceptionHandler(Throwable.class)
    public String handleThrowble(){
        return "redirect:/error";
    }

}
