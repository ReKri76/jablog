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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/extend-accont")
    public ResponseEntity<Void> extendAccount(@Valid @RequestBody Login login) {

        String accountName;
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth!=null)
            accountName = (String) auth.getPrincipal();
        else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access token must be not null");

        loginService.extendAccount(login, accountName);

        return ResponseEntity.ok().build();
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class AccountResponse extends SimpleResponse{
        private String token;
    }

    @PostMapping("/create-account")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody Login login){

        Tokens tokens = loginService.createAccount(login);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMillis(SecurityConfig.REFRESH_EXPIRED_TIME))
                .sameSite("Lax")
                .build();

        AccountResponse res = new AccountResponse();
        res.setToken(tokens.getAccessToken());
        res.setStatus(201);
        res.setMessage("ok");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(res);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccountResponse> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token must be not null");

        Tokens tokens = loginService.refresh(refreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMillis(SecurityConfig.REFRESH_EXPIRED_TIME))
                .sameSite("Lax")
                .build();

        AccountResponse res = new AccountResponse();
        res.setToken(tokens.getAccessToken());
        res.setStatus(200);
        res.setMessage("ok");

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(res);
    }

    @PostMapping("/log-in")
    public ResponseEntity<AccountResponse> login(@Valid @RequestBody Login login){

        Tokens tokens = loginService.login(login);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMillis(SecurityConfig.REFRESH_EXPIRED_TIME))
                .sameSite("Lax")
                .build();

        AccountResponse res = new AccountResponse();
        res.setToken(tokens.getAccessToken());
        res.setStatus(200);
        res.setMessage("ok");

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(res);
    }
}