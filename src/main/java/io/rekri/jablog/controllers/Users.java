package io.rekri.jablog.controllers;

import io.rekri.jablog.DTO.Login;
import io.rekri.jablog.config.security.CustomUserDetails;
import io.rekri.jablog.config.security.Roles;
import io.rekri.jablog.service.UsersService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class Users {

    private final UsersService usersService;

    @PostMapping(value = "/panel/{boardName}/add")
    public String addUser(@PathVariable("boardName") String boardName, @Valid @ModelAttribute("login") Login login) {

        usersService.addUser(boardName, login);

        return "redirect:/users/panel/"+boardName;
    }

    @DeleteMapping(value = "/panel/{boardName}/{nickname}")
    public ResponseEntity<Void> deleteUser(@PathVariable("boardName") String boardName, @PathVariable("nickname") String nickname) {

        usersService.deleteUser(nickname, boardName);

        return ResponseEntity
                .ok()
                .header("HX-Redirect", "/users/panel/"+boardName)
                .build();
    }

    @GetMapping(value = "/panel/{boardName}")
    public String viewUsers(@PathVariable("boardName") String boardName, Model model) {

        final List<String> usersName = usersService.viewUsers(boardName);

        model.addAttribute("users", usersName);
        model.addAttribute("boardName", boardName);
        model.addAttribute("login", new Login());

        return "users";
    }

    @GetMapping(value = "/panel") //на этой странице есть поле для логина и для управления в залогиненных досках как админ
    public String panel(Model model, HttpSession session){

        final Enumeration<String> boards = session.getAttributeNames();
        final ArrayList<String> boardNames = new ArrayList<String>();

        while (boards.hasMoreElements()){
            final String boardName = boards.nextElement();
            if(boardName.length()>3)
                continue;

            final CustomUserDetails user = (CustomUserDetails) session.getAttribute(boardName);
            if (user == null)
                continue;

            if (user.getRole().equals(Roles.ROLE_ADMIN))
                boardNames.add(boardName);
        }

        model.addAttribute("boardNames", boardNames);
        model.addAttribute("login", new Login());

        return "panel";
    }
}
