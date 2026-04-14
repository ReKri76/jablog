package com.example.jablog.config;

import com.example.jablog.config.security.CustomAuthorizationManager;
import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.entity.Board;
import com.example.jablog.entity.Users;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
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
    public SecurityFilterChain securityFilterChain(@NonNull HttpSecurity http){

        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(
                        (req, res, ex) ->
                                res.sendRedirect("/")
                ))

                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((req, res, accessDeniedException) ->
                            res.sendRedirect("/")
                        )
                )

                .anonymous(anonymous -> anonymous
                        .principal(createDefaultUser())
                        .authorities("ROLE_ANON")
                )
                
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,  "/").permitAll()

                        .requestMatchers(HttpMethod.GET,"/{boardName}/**").access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.GET,"/{boardName}/{thread}/**").access(customAuthorizationManager)

                        .requestMatchers(HttpMethod.POST, "/poster/{boardName}/**").access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.POST, "/poster/{boardName}/{thread}/**").access(customAuthorizationManager)

                        .requestMatchers(HttpMethod.DELETE, "/deleter/{boardName}/**").access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.DELETE, "/deleter/{boardName}/{thread}/**").access(customAuthorizationManager)

                        .requestMatchers(HttpMethod.DELETE, "/users/{boardName}/**").access(customAuthorizationManager)
                        .requestMatchers(HttpMethod.POST, "/users/{boardName}**").access(customAuthorizationManager)

                        .anyRequest().denyAll()
                );

        return http.build();
    }

    private CustomUserDetails createDefaultUser(){

        Users user = new Users();
        user.setId(0);
        user.setRole(false);
        user.setNickname("ANON");
        user.setPassword("{noop}");

        Board board = new Board();
        board.setName("ANON");
        board.setRules(new String[12]);

        user.setBoard(board);

        CustomUserDetails customUserDetails = CustomUserDetails.build(user);
        customUserDetails.setRole("ROLE_ANON");

        return customUserDetails;
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
