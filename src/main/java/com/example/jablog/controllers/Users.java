package com.example.jablog.controllers;

import com.example.jablog.service.UsersService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/user")
public class Users {

    private final UsersService usersService;

    @PostMapping(value = "/user/{boardName}")
    public String addUser(@PathVariable("boardName") @NonNull String boardName,
                        @RequestParam("pass") @NonNull String pass, @RequestParam("nickname") @NonNull String nickname){

        usersService.addUser(boardName, nickname, pass);

        return "redirect:/"+boardName;
    }

    @DeleteMapping(value = "/user/{boardName}")
    public String deleteUser(@PathVariable("boardName") String boardName, @RequestParam("nickname") String nickname){

        usersService.deleteUser(nickname);

        return "redirect:/"+boardName;
    }

}
