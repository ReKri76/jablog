package com.example.jablog.controllers;

import com.example.jablog.service.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.WebUtils;

import java.time.Duration;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class API {

    private final JWTService jwtService;

    @PostMapping(value="/refresh/{boardName}")
    public String refresh(@PathVariable("boardName") String boardName, HttpServletRequest req, HttpServletResponse res){

        Cookie cookie = WebUtils.getCookie(req, "REFRESH-"+ boardName);

        if (cookie == null)
            return "redirect:/"+boardName;

        String refresh = cookie.getValue();

        String access = jwtService.getAccessByRefresh(refresh);

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
                .path("/api/refresh"+boardName)
                .sameSite("Lax")
                .maxAge(Duration.ofDays(36).getSeconds() + Duration.ofHours(12).getSeconds())
                .build();

        //TODO: при деплое поменять на отправку по https

        res.addHeader("Set-Cookie", accessCookie.toString());
        res.addHeader("Set-Cookie", refreshCookie.toString());

        return "redirect:/"+boardName;
    }

}
