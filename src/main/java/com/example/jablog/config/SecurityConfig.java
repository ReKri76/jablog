package com.example.jablog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()           // ← Всё разрешено без авторизации
                )
                .csrf(csrf -> csrf.disable())           // Отключаем CSRF (для разработки удобно)
                .formLogin(form -> form.disable())      // Отключаем форму логина
                .httpBasic(httpBasic -> httpBasic.disable()); // Отключаем Basic Auth

        return http.build();
    }
}
