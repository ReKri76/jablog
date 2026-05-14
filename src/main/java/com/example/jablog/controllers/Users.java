package com.example.jablog.controllers;

import com.example.jablog.DTO.Login;
import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.service.UsersService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Enumeration;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class Users {

    private final UsersService usersService;

    @PostMapping(value = "/panel/{boardName}")
    public ResponseEntity<String> addUser(@PathVariable("boardName") @NonNull String boardName,
                          @Valid @ModelAttribute("login") Login login) {

        usersService.addUser(boardName, login);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/users/panel/"+boardName)
                .body("/users/panel/"+boardName);
    }

    @DeleteMapping(value = "/panel/{boardName}/{nickname}")
    public ResponseEntity<Void> deleteUser(@PathVariable("boardName") String boardName,
                                           @PathVariable("nickname") String nickname) {

        usersService.deleteUser(nickname, boardName);

        return ResponseEntity
                .ok()
                .header("HX-Redirect", "/users/panel/"+boardName)
                .build();
    }

    public record Users_Panel_boardName(
            ArrayList<String> userNames,
            String boardName
    ){}

    @GetMapping(value = "/panel/{boardName}")
    public ResponseEntity<Users_Panel_boardName> viewUsers(@PathVariable("boardName") String boardName) {

        final ArrayList<String> usersName = usersService.viewUsers(boardName);

        final Users_Panel_boardName record = new Users_Panel_boardName(usersName, boardName);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Cache-Control", "max-age=3600")
                .body(record);
    }

    public record Users_Panel(
            ArrayList<String> boardNames
    ){}

    @GetMapping(value = "/panel")
    public ResponseEntity<Users_Panel> panel(HttpSession session){

        final Enumeration<String> boards = session.getAttributeNames();
        final ArrayList<String> boardNames = new ArrayList<String>();

        while (boards.hasMoreElements()){
            final String boardName = boards.nextElement();
            if(boardName.length()>3)
                continue;

            final CustomUserDetails user = (CustomUserDetails) session.getAttribute(boardName);
            if (user == null)
                continue;

            if (user.getRole().equals("ROLE_ADMIN"))
                boardNames.add(boardName);
        }

        final Users_Panel record = new Users_Panel(boardNames);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Cache-Control", "max-age=3600")
                .body(record);
    }
}
