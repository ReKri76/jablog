package io.rekri.jablog.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.rekri.jablog.config.SecurityConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JWTService {

    @Value("${jwt.key}")
    private String secretBase64;

    private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));

    @NotNull
    public String generateAccessToken(@NotNull String accountName){
        final Date expiry = Date.from(Instant.ofEpochMilli(SecurityConfig.ACCESS_EXPIRED_TIME + Instant.now().toEpochMilli()));

        return Jwts.builder()
                .subject(accountName)
                .issuedAt(Date.from(Instant.now()))
                .expiration(expiry)
                .id(UUID.randomUUID().toString())
                .signWith(key)
                .compact();
    }

    @NotNull
    public String generateRefreshToken(@NotNull String accountName){
        final Date expiry = Date.from(Instant.ofEpochMilli(SecurityConfig.REFRESH_EXPIRED_TIME + Instant.now().toEpochMilli()));

        return Jwts.builder()
                .subject(accountName)
                .issuedAt(Date.from(Instant.now()))
                .expiration(expiry)
                .id(UUID.randomUUID().toString())
                .signWith(key)
                .compact();
    }

    @NotNull
    public Claims parseToken (@NotNull String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
