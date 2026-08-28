package io.rekri.jablog.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.rekri.jablog.DTO.Login;
import io.rekri.jablog.DTO.Tokens;
import io.rekri.jablog.config.SecurityConfig;
import io.rekri.jablog.entity.Accounts;
import io.rekri.jablog.entity.Users;
import io.rekri.jablog.errors.NicknameAlreadyUsedException;
import io.rekri.jablog.repository.LoginRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    @Transactional
    public void extendAccount (@NotNull Login login, @NotNull String accountName) throws NoResultException {

        log.info("User {} is starting login", login.getNickname());

        Users user;

        try {
            user = loginRepository.login(login.getNickname());
        } catch (NoResultException e){
            log.warn("User {} not found. Error: {}", login.getNickname(), e.getMessage());
            throw new BadCredentialsException("Invalid user or password.");
        }

        if (!passwordEncoder.matches(login.getPassword(), user.getPassword())) {
            log.warn("User {} password does not match password.", login.getNickname());
            throw new BadCredentialsException("Invalid user or password.");
        }

        loginRepository.extendAccount(user, accountName);

        log.info("User {} is log in by {} account", login.getNickname(), accountName);
    }

    @NotNull
    @Transactional
    public Tokens createAccount(@NotNull Login login){

        log.info("Start adding {} account", login.getNickname());

        if (loginRepository.isAccountNameAlreadyUsed(login.getNickname()))
            throw new NicknameAlreadyUsedException("Nickname is already taken: " + login.getNickname());

        login.setPassword(passwordEncoder.encode(login.getPassword()));

        loginRepository.createAccount(login);

        Tokens res = new Tokens(
                jwtService.generateAccessToken(login.getNickname()),
                jwtService.generateRefreshToken(login.getNickname())
        );

        log.info("Account {} was created", login.getNickname());

        return res;
    }

    @NotNull
    @Transactional
    public Tokens refresh(@NotNull String refreshToken){

        String nickname;

        try {
            Claims claims = jwtService.parseToken(refreshToken);
            nickname = claims.getSubject();
        } catch (JwtException e){
            throw new BadCredentialsException("invalid token");
        }

        Accounts account = loginRepository.findAccountByUsername(nickname)
                .orElseThrow(() -> new BadCredentialsException("Account not found"));

        long now = Instant.now().toEpochMilli();

        if (account.getRefreshExpiredTime() < now) {
            log.warn("Refresh token for {} is expired according to DB record", nickname);
            throw new BadCredentialsException("Refresh token expired");
        }

        Tokens newTokens = new Tokens(
                jwtService.generateAccessToken(nickname),
                jwtService.generateRefreshToken(nickname)
        );

        loginRepository.updateRefreshExpiredTime(account, SecurityConfig.REFRESH_EXPIRED_TIME + now);

        log.info("Tokens for account {} were refreshed", nickname);

        return newTokens;
    }

    @NotNull
    @Transactional
    public Tokens login(Login login){

        log.info("Start log-in user {}", login.getNickname());

        Optional<Accounts> accounts = loginRepository.findAccountByUsername(login.getNickname());

        if (accounts.isEmpty())
            throw new IllegalArgumentException("Invalid name or password");

        if (!passwordEncoder.matches(login.getPassword(), accounts.get().getPassword()))
            throw new IllegalArgumentException("Invalid name or password");

        loginRepository.updateRefreshExpiredTime(accounts.get(),
                SecurityConfig.REFRESH_EXPIRED_TIME + Instant.now().toEpochMilli());

        Tokens res = new Tokens(
                jwtService.generateAccessToken(login.getNickname()),
                jwtService.generateRefreshToken(login.getNickname())
        );

        log.info("End log-in user {}", login.getNickname());

        return res;
    }
}