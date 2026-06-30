package com.example.jablog.controllers;

import com.example.jablog.DTO.Login;
import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.service.APIService;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Вспомогательные публичные эндпоинты
 * */
@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class API {

    private final APIService apiService;

    @GetMapping("/login")
    public String loginPage() {
        log.debug("/api/login page is required");
        return "login";
    }

    @PostMapping("/login/verify")
    public String login(HttpSession session, @Valid @ModelAttribute("login") Login login, BindingResult result) {

        log.debug("/api/login/verify page is required");

        if (result.hasErrors())
            return "redirect:/api/login";

        CustomUserDetails customUserDetails;
        try {
            customUserDetails = apiService.login(login);
        } catch (NoResultException e) {
            return "redirect:/api/login";
        }

        final String boardName = customUserDetails.getBoardName();

        session.setAttribute(boardName, customUserDetails);

        return "redirect:/" + boardName;
    }
}
