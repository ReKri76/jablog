package io.rekri.jablog.controllers;

import io.rekri.jablog.DTO.Login;
import io.rekri.jablog.config.security.CustomUserDetails;
import io.rekri.jablog.service.APIService;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void likeThread(@PathVariable int threadId){
        apiService.likeThread(threadId);
    }

    @PatchMapping("/carma/minus/{boardName}/{threadId}")
    public void dislikeThread(@PathVariable int threadId){
        apiService.dislikeThread(threadId);
    }
}
