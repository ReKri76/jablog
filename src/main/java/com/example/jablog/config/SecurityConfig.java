package com.example.jablog.config;

import com.example.jablog.config.security.CustomAuthorizationManager;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthorizationManager customAuthorizationManager;

    @Bean
    public SecurityFilterChain securityFilterChain(@NonNull HttpSecurity http) {

        http
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)

                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'none'; " +
                                        "script-src 'none'; " +
                                        "object-src 'none'; " +
                                        "style-src 'self'; " +
                                        "img-src http://localhost:9000 'self'; " +
                                        "connect-src 'none'; " +
                                        "font-src 'self'; " +
                                        "frame-ancestors 'none'; " +
                                        "base-uri 'self'; " +
                                        "form-action 'self'; " +
                                        "upgrade-insecure-requests")
                        )

                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                        )
                )

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**", "/", "/poster/board")
                )

                .sessionManagement(session ->

                        session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)

                        .sessionConcurrency(concurrency -> concurrency
                                .maximumSessions(1)
                        )

                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::changeSessionId)
                )

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .anonymous(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/login/verify").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/poster/board").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/panel").permitAll()

                        .requestMatchers(HttpMethod.POST, "/poster/{boardName}/{thread}")
                            .access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.POST, "/poster/{boardName}")
                            .access(customAuthorizationManager)

                        .requestMatchers(HttpMethod.DELETE, "/deleter/{boardName}/{thread}")
                            .access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.DELETE, "/deleter/{boardName}/{thread}/{post}")
                            .access(customAuthorizationManager)

                        .requestMatchers(HttpMethod.DELETE, "/users/{boardName}")
                            .access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.POST, "/users/{boardName}")
                            .access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/users/{boardName}")
                            .access(customAuthorizationManager)

                        .requestMatchers(HttpMethod.GET, "/{boardName}/{thread}")
                            .access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/{boardName}")
                            .access(customAuthorizationManager)

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

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config){
        return config.getAuthenticationManager();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}