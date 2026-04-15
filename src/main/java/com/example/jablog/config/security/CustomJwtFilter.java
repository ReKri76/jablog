package com.example.jablog.config.security;

import com.example.jablog.service.JWTService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class CustomJwtFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String boardName = getBoardNameFromRequest(request);

        Cookie accessCookie = WebUtils.getCookie(request, "ACCESS-"+ boardName);

        if (accessCookie == null)
            doFilter(request, response, filterChain);

        String jwt = accessCookie.getValue();
        Claims claims = jwtService.getClaims(jwt);

        if (claims.getExpiration().before(new Date()))
            response.sendRedirect("/api/refresh"+boardName);

        String[] rules = claims.get("boardRules", String[].class);

        CustomUserDetails userDetails = new CustomUserDetails(
                claims.get("boardName", String.class),
                rules,
                "",
                claims.getSubject(),
                claims.get("role", String.class)
        );

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }

    private String getBoardNameFromRequest(HttpServletRequest req){
        String[] parts = req.getRequestURI().split("/");

        return parts[1].length()>3 ? parts[2] : parts[1];
    }

}
