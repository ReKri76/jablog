package com.example.jablog.controllers;

import com.example.jablog.service.UsersService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class Users {

    private final UsersService usersService;

    @PostMapping(value = "/{boardName}")
    public String addUser(@PathVariable("boardName") @NonNull String boardName,
                          @RequestParam("pass") @NonNull String pass, @RequestParam("nickname") @NonNull String nickname) {

        usersService.addUser(boardName, nickname, pass);

        return "redirect:/users/"+boardName;
    }

    @DeleteMapping(value = "/{boardName}")
    public String deleteUser(@PathVariable("boardName") String boardName, @RequestParam("nickname") String nickname) {

        usersService.deleteUser(nickname);

        return "redirect:/users/"+boardName;
    }

    @GetMapping(value = "/{boardName}")
    public String viewUsers(@PathVariable("boardName") String boardName, Model model) {

        model.addAttribute("users", usersService.viewUsers(boardName));

        return "users";
    }
}