package com.example.jablog.controllers;

import com.example.jablog.DTO.Login;
import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.service.APIService;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        return "login";
    }

    @PostMapping("/login/verify")
    public String login(HttpSession session, @Valid @ModelAttribute("login") Login login) {

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

    @PatchMapping("/carma/plus/{boardName}/{threadId}")
    public ResponseEntity<Void> likeThread(@PathVariable int threadId){
        apiService.likeThread(threadId);
        return ResponseEntity.ok().header("HX-Refresh", "true").build();
    }

    @PatchMapping("/carma/minus/{boardName}/{threadId}")
    public ResponseEntity<Void> dislikeThread(@PathVariable int threadId){
        apiService.dislikeThread(threadId);
        return ResponseEntity.ok().header("HX-Refresh", "true").build();
    }
}
