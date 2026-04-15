package com.example.jablog.config.security;

import com.example.jablog.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    final private JWTService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth) throws IOException {

        CustomUserDetails customUserDetails = (CustomUserDetails) Objects.requireNonNull(auth.getPrincipal());
        String boardName = customUserDetails.getBoardName();

        String access = jwtService.generateAccess(customUserDetails);
        String refresh = jwtService.generateRefresh(customUserDetails);

        ResponseCookie accessCookie = ResponseCookie.from("ACCESS-"+boardName, access)
                .httpOnly(true)
                .secure(false)
                .path("/"+boardName)
                .sameSite("Lax")
                .maxAge(Duration.ofDays(1).getSeconds())
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH-"+boardName, refresh)
                .httpOnly(true)
                .secure(false)
                .path("/api/refresh")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(36).getSeconds() + Duration.ofHours(12).getSeconds())
                .build();

        //TODO: при деплое поменять на отправку по https

        res.addHeader("Set-Cookie", accessCookie.toString());
        res.addHeader("Set-Cookie", refreshCookie.toString());
        res.sendRedirect("/"+boardName);
    }
}
