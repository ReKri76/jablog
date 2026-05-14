package com.example.jablog.controllers;

import com.example.jablog.DTO.Login;
import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.service.APIService;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class API {

    private final APIService apiService;

    /*
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    */

    @PostMapping("/login/verify")
    public ResponseEntity<String> login(HttpSession session,
                                @Valid @ModelAttribute("login") Login login) {

        CustomUserDetails customUserDetails;
        try {
            customUserDetails = apiService.login(login);
        } catch (NoResultException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header("Location", "/api/login")
                    .body("/api/login");
        }

        final String boardName = customUserDetails.getBoardName();

        session.setAttribute(boardName, customUserDetails);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Location", "/api/login")
                .body("/api/login");
    }
}
