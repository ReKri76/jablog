package io.rekri.jablog.config;

import io.rekri.jablog.config.security.*;
import io.rekri.jablog.config.security.DeleterAuthorizationManager;
import io.rekri.jablog.config.security.GetterAuthorizationManager;
import io.rekri.jablog.config.security.PosterAuthorizationManager;
import io.rekri.jablog.config.security.UsersAuthorizationManager;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
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

    private final PosterAuthorizationManager posterAuthorizationManager;
    private final DeleterAuthorizationManager deleterAuthorizationManager;
    private final UsersAuthorizationManager usersAuthorizationManager;
    private final GetterAuthorizationManager getterAuthorizationManager;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Bean
    public SecurityFilterChain securityFilterChain(@NonNull HttpSecurity http) {

        http
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)

                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'none'; " +
                                        "script-src 'self' 'unsafe-inline'; " +
                                        "object-src 'none'; " +
                                        "style-src 'self' 'unsafe-inline'; " +
                                        "img-src " + minioEndpoint + " 'self' data:; " +
                                        "connect-src 'self'; " +
                                        "font-src 'self'; " +
                                        "frame-ancestors 'none'; " +
                                        "base-uri 'self'; " +
                                        "form-action 'self'; " +
                                        "upgrade-insecure-requests"
                                )
                        )

                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                        )
                )

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**", "/", "/poster/board", "/script/**", "/styles/**", "/error")
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
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                        .requestMatchers(HttpMethod.GET, "/script/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/styles/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/error").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/login/verify").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/poster/board").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/panel").permitAll()

                        .requestMatchers(HttpMethod.POST, "/poster/{boardName}/{thread}")
                            .access(posterAuthorizationManager)
                        .requestMatchers(HttpMethod.POST, "/poster/{boardName}")
                            .access(posterAuthorizationManager)

                        .requestMatchers(HttpMethod.DELETE, "/deleter/{boardName}/{thread}")
                            .access(deleterAuthorizationManager)
                        .requestMatchers(HttpMethod.DELETE, "/deleter/{boardName}/{thread}/{post}")
                            .access(deleterAuthorizationManager)

                        .requestMatchers(HttpMethod.DELETE, "/users/panel/{boardName}/{nickname}")
                            .access(usersAuthorizationManager)
                        .requestMatchers(HttpMethod.POST, "/users/panel/{boardName}/add")
                            .access(usersAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/users/panel/{boardName}")
                            .access(usersAuthorizationManager)

                        .requestMatchers(HttpMethod.GET, "/{boardName}/img/{fileName}")
                            .access(getterAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/{boardName}/{thread}")
                            .access(getterAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/{boardName}")
                            .access(getterAuthorizationManager)

                        .requestMatchers(HttpMethod.PATCH, "/api/carma/plus/{boardName}/{thread}")
                            .access(getterAuthorizationManager)
                        .requestMatchers(HttpMethod.PATCH, "/api/carma/minus/{boardName}/{thread}")
                            .access(getterAuthorizationManager)

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
