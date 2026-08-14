package io.rekri.jablog.controllers;

import io.rekri.jablog.config.security.CustomUserDetails;
import io.rekri.jablog.service.LoginService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/verify")
    public ResponseEntity<Void> login(HttpSession session, @Valid @RequestBody io.rekri.jablog.DTO.Login login) {

        CustomUserDetails customUserDetails = loginService.login(login);

        final String boardName = customUserDetails.getBoardName();

        session.setAttribute(boardName, customUserDetails);

        return ResponseEntity.ok().build();
    }
}
