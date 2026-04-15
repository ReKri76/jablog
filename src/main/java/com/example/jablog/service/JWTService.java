package com.example.jablog.service;

import com.example.jablog.config.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JWTService {

    @Value("${jwt.key}")
    private String secret;

    private final long accessTime = Duration.ofDays(1).toMillis();
    private final long  refreshTime = Duration.ofDays(36).toMillis() + Duration.ofHours(12).toMillis();

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateAccess(CustomUserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .claim("boardName", user.getBoardName())
                .claim("boardRules", user.getBoardRules())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTime))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefresh(CustomUserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .claim("boardName", user.getBoardName())
                .claim("boardRules",user.getBoardRules())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTime))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getAccessByRefresh(String refresh){
        Claims claims = this.getClaims(refresh);

        return Jwts.builder()
                .subject(claims.getSubject())
                .claim("role", claims.get("role"))
                .claim("boardName", claims.get("boardName"))
                .claim("boardRules", claims.get("boardRules"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTime))
                .signWith(getSigningKey())
                .compact();
    }
}
