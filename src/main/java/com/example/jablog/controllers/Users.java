package com.example.jablog.controllers;

import com.example.jablog.DTO.Login;
import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.service.UsersService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Enumeration;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class Users {

    private final UsersService usersService;

    @PostMapping(value = "/{boardName}")
    public String addUser(@PathVariable("boardName") @NonNull String boardName,
                          @Valid @ModelAttribute("login") Login login) {

        usersService.addUser(boardName, login);

        return "redirect:/users/"+boardName;
    }

    @DeleteMapping(value = "/{boardName}")
    public ResponseEntity<Void> deleteUser(@PathVariable("boardName") String boardName, @PathVariable("nickname") String nickname) {

        usersService.deleteUser(nickname);

        return ResponseEntity
                .ok()
                .header("HX-Redirect", "/users/"+boardName)
                .build();
    }

    @GetMapping(value = "/{boardName}")
    public String viewUsers(@PathVariable("boardName") String boardName, Model model) {

        ArrayList<String> usersName = usersService.viewUsers(boardName);

        model.addAttribute("users", usersName);
        model.addAttribute("boardName", boardName);

        return "users";
    }

    @GetMapping(value = "/panel")
    public String panel(Model model, HttpSession session){

        Enumeration<String> boards = session.getAttributeNames();
        ArrayList<String> boardNames = new ArrayList<String>();

        while (boards.hasMoreElements()){
            String boardName = boards.nextElement();
            CustomUserDetails user = (CustomUserDetails) session.getAttribute(boardName);
            if (user.getRole().equals("ROLE_ADMIN"))
                boardNames.add(boardName);
        }

        model.addAttribute("boardNames", boardNames);
        model.addAttribute("login", new Login());

        return "panel";
    }
}