package com.example.jablog.controllers;

import com.example.jablog.entity.Board;
import com.example.jablog.service.GetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.LinkedList;

@Controller
@RequiredArgsConstructor
public class Getter {

    GetterService getterService;

    @GetMapping("/{boardName}")
    public String board(@PathVariable String boardName, Model model){

        return "board";
    }

    @GetMapping("/{bordName}/{threadId}")
    public String thread(@PathVariable String boardName, @PathVariable String threadId, Model model){

        return "thread";
    }

    @GetMapping("/")
    public String start(Model model){

        LinkedList<Board> boards= getterService.start();

        return "index";
    }

}
