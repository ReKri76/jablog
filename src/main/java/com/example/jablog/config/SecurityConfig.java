package com.example.jablog.config;

import com.example.jablog.config.security.CustomAuthorizationManager;
import com.example.jablog.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthorizationManager customAuthorizationManager;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(@NonNull HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )

                .sessionManagement(session ->

                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)

                        .sessionConcurrency(concurrency -> concurrency
                                .maximumSessions(1)
                        )

                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::changeSessionId)
                )

                .anonymous(anonymous -> anonymous
                        .principal(customUserDetailsService.createDefault())
                        .authorities("ROLE_ANON")
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/poster/board").permitAll()

                        .requestMatchers(HttpMethod.POST, "/poster/{boardName}/{thread}")
                            .access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.POST, "/poster/{boardName}")
                            .access(customAuthorizationManager)

                        .requestMatchers(HttpMethod.DELETE, "/deleter/{boardName}/{thread}")
                            .access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.DELETE, "/deleter/{boardName}/{thread}/{post}")
                            .access(customAuthorizationManager)

                        .requestMatchers(HttpMethod.DELETE, "/users/{boardName}/**")
                            .access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.POST, "/users/{boardName}**")
                            .access(customAuthorizationManager)

                        .requestMatchers(HttpMethod.GET, "/{boardName}/{thread}")
                            .access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/{boardName}")
                            .access(customAuthorizationManager)

                        .anyRequest().denyAll()
                );

        http.addFilterAfter(boardContextPreparationFilter(), AnonymousAuthenticationFilter.class);


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