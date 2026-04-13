package com.example.jablog.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    final private CustomAuthorizationManager customAuthorizationManager;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                            response.sendRedirect("/")
                        )
                )
                
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,  "/").permitAll()
                        .requestMatchers(HttpMethod.GET,"/{boardName}/**").access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.POST, "/poster/{boardName}/**").access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.DELETE, "/deleter/{boardName}/**").access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.DELETE, "/users/{boardName}/**").access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.POST, "/users/{boardName}**").access(customAuthorizationManager)
                        .anyRequest().denyAll()
                );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(
                16,
                32,
                1,
                1 << 16,
                3
        );
    }
}
