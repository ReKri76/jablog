package io.rekri.jablog.controllers;

import io.rekri.jablog.DTO.Login;
import io.rekri.jablog.DTO.SimpleResponse;
import io.rekri.jablog.DTO.Tokens;
import io.rekri.jablog.config.SecurityConfig;
import io.rekri.jablog.service.LoginService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/extend-accont")
    public ResponseEntity<Void> login(@Valid @RequestBody Login login) {

        String accountName;
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth!=null)
            accountName = (String) auth.getPrincipal();
        else
            throw new BadCredentialsException("Jwt is required");

        loginService.login(login, accountName);

        return ResponseEntity.ok().build();
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class createAccountResponse extends SimpleResponse{
        private String token;
    }

    @PostMapping("/create-account")
    public ResponseEntity<createAccountResponse> createAccount(@Valid @RequestBody Login login){

        Tokens tokens = loginService.createAccount(login);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(SecurityConfig.REFRESH_EXPIRED_TIME + Instant.now().toEpochMilli())
                .sameSite("Lax")
                .build();

        createAccountResponse res = new createAccountResponse();
        res.setToken(tokens.getAccessToken());
        res.setStatus(201);
        res.setMessage("ok");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(res);
    }
}
