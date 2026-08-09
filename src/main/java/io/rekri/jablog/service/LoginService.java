package io.rekri.jablog.service;

import io.rekri.jablog.DTO.Login;
import io.rekri.jablog.config.security.CustomUserDetails;
import io.rekri.jablog.entity.Users;
import io.rekri.jablog.repository.LoginRepository;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    @NotNull
    public CustomUserDetails login (@NotNull Login login) throws NoResultException {

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

        log.info("User {} is log in", login.getNickname());

        return customUserDetailsService.build(user);
    }
}
