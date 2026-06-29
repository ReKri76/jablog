package com.example.jablog.service;

import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.config.security.Roles;
import com.example.jablog.entity.Board;
import com.example.jablog.entity.Users;
import com.example.jablog.repository.UserDetailsRepository;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
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

        Users user = new Users();
        CustomUserDetails customUserDetails;

        try {
            user = userDetailsRepository.user(username);
            customUserDetails = CustomUserDetails.build(user);
        } catch (NoResultException e ){
            user.setId(0);
            user.setRole(false);
            user.setNickname("ANON");
            user.setPassword("{noop}");

            final Board board = new Board();
            board.setName("ANON");
            board.setRules("------------");

            user.setBoard(board);

            customUserDetails = CustomUserDetails.build(user);
            customUserDetails.setRole(Roles.ROLE_ANON);
        }

        return customUserDetails;
    }

    public @NonNull CustomUserDetails createDefault(){
        return new CustomUserDetails(
                "ANON",
                "------------",
                "{noop}",
                "ANON",
                Roles.ROLE_ANON
        );
    }
}
