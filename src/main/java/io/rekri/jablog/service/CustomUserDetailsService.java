package io.rekri.jablog.service;

import io.rekri.jablog.config.security.CustomUserDetails;
import io.rekri.jablog.config.security.Roles;
import io.rekri.jablog.entity.Board;
import io.rekri.jablog.entity.Users;
import io.rekri.jablog.repository.UserDetailsRepository;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDetailsRepository userDetailsRepository;

    @Override
    @NotNull
    public UserDetails loadUserByUsername(@NotNull String username) throws UsernameNotFoundException {

        CustomUserDetails customUserDetails;

        try {
            Users user = userDetailsRepository.user(username);
            customUserDetails = build(user);
        } catch (NoResultException e ){
            customUserDetails = createDefault();
        }

        return customUserDetails;
    }

    @NotNull
    public CustomUserDetails build(@NotNull Users users){
        final Board board = users.getBoard();

        return new CustomUserDetails(
                board.getName(),
                board.getRules(),
                users.getPassword(),
                users.getNickname(),
                users.isRole() ? Roles.ROLE_ADMIN : Roles.ROLE_GROUP
        );
    }

    @NotNull
    public CustomUserDetails createDefault(){
        return new CustomUserDetails(
                "ANON",
                "------------",
                "{noop}",
                "ANON",
                Roles.ROLE_ANON
        );
    }
}
