package io.rekri.jablog.config;

import io.rekri.jablog.config.security.authorizations.DeleterAuthorizationManager;
import io.rekri.jablog.config.security.authorizations.GetterAuthorizationManager;
import io.rekri.jablog.config.security.authorizations.PosterAuthorizationManager;
import io.rekri.jablog.config.security.authorizations.UsersAuthorizationManager;
import io.rekri.jablog.config.security.filters.ErrorsFilter;
import io.rekri.jablog.config.security.filters.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

import static org.springframework.security.crypto.argon2.Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PosterAuthorizationManager posterAuthorizationManager;
    private final DeleterAuthorizationManager deleterAuthorizationManager;
    private final UsersAuthorizationManager usersAuthorizationManager;
    private final GetterAuthorizationManager getterAuthorizationManager;

    public static final long ACCESS_EXPIRED_TIME = Duration.ofHours(1).toMillis();
    public static final long REFRESH_EXPIRED_TIME = Duration.ofDays(7).toMillis();

    @Bean
    public SecurityFilterChain securityFilterChain(@NonNull HttpSecurity http, JwtFilter jwtFilter, ErrorsFilter errorsFilter) {

        http
                .cors(Customizer.withDefaults())
                .headers(headers -> headers

                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("""
                                        default-src 'none';
                                        script-src 'self';
                                        style-src 'self';
                                        connect-src 'self';
                                        font-src 'self';
                                        frame-ancestors 'none';
                                        base-uri 'self';
                                        form-action 'self';
                                        upgrade-insecure-requests;
                                       """
                                )
                        )

                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                        )
                )

                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        .ignoringRequestMatchers("/api/public/**", "/api/poster/board")
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .anonymous(AbstractHttpConfigurer::disable)

                .addFilterBefore(errorsFilter, JwtFilter.class)

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.POST, "/api/public/login/verify").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/poster/board").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/panel").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/poster/{boardName}/{thread}")
                            .access(posterAuthorizationManager)
                        .requestMatchers(HttpMethod.POST, "/api/poster/{boardName}")
                            .access(posterAuthorizationManager)

                        .requestMatchers(HttpMethod.DELETE, "/api/deleter/{boardName}/{thread}")
                            .access(deleterAuthorizationManager)
                        .requestMatchers(HttpMethod.DELETE, "/api/deleter/{boardName}/{thread}/{post}")
                            .access(deleterAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/api/deleter/{boardName}/{thread}")
                            .access(deleterAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/api/deleter/{boardName}/{thread}/{post}")
                            .access(deleterAuthorizationManager)

                        .requestMatchers(HttpMethod.DELETE, "/api/users/panel/{boardName}/{nickname}")
                            .access(usersAuthorizationManager)
                        .requestMatchers(HttpMethod.POST, "/api/users/panel/{boardName}/add")
                            .access(usersAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/api/users/panel/{boardName}")
                            .access(usersAuthorizationManager)

                        .requestMatchers(HttpMethod.GET, "/api/{boardName}/img/{fileName}")
                            .access(getterAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/api/{boardName}/{thread}")
                            .access(getterAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/api/{boardName}")
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
        return defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config){
        return config.getAuthenticationManager();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${client.host}") String frontHost) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(frontHost));
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE"));
        config.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}