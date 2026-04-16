package com.example.jablog.controllers;

import com.example.jablog.DTO.Login;
import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.service.APIService;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class API {

    private final APIService apiService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login/verify")
    public String login(HttpServletRequest req,
                        @Valid @ModelAttribute("login") Login login) {

        CustomUserDetails customUserDetails;
        try {
            customUserDetails = apiService.login(login);
        } catch (NoResultException e) {
            return "redirect:/api/login?error=true";
        }

        String boardName = customUserDetails.getBoardName();

        // ← САМОЕ ГЛАВНОЕ: сохраняем в сессию
        req.getSession(true).setAttribute("boardAuth." + boardName, customUserDetails);

        return "redirect:/" + boardName;
    }
}