package com.example.jablog.service;

import com.example.jablog.entity.Users;
import com.example.jablog.repository.UserDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDetailsRepository userDetailsRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        Users user = userDetailsRepository.user(username);

        return User
                .withUsername(user.getNickname())
                .password(user.getPassword())
                .authorities(user.isRole() ? "ROLE_ADMIN" : "ROLE_GROUP")
                .build();
    }
}
