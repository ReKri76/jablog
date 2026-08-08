package io.rekri.jablog.unit;

import io.rekri.jablog.config.security.CustomUserDetails;
import io.rekri.jablog.config.security.Roles;
import io.rekri.jablog.entity.Board;
import io.rekri.jablog.entity.Users;
import io.rekri.jablog.repository.UserDetailsRepository;
import io.rekri.jablog.service.CustomUserDetailsService;
import jakarta.persistence.NoResultException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Spy
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void loadUserByUserName_SuccessfullyFindInDataBase(){
        final String userName = "tester";
        final String rules = "rwdxrwdxrwdx";
        Users mockUser = createUser(userName, rules);
        when(userDetailsRepository.user(userName)).thenReturn(mockUser);

        CustomUserDetails res = (CustomUserDetails) customUserDetailsService.loadUserByUsername(userName);

        assertNotNull(res);
        assertEquals(Roles.ROLE_GROUP, res.getRole());
        assertEquals(userName, res.getUsername());
        assertEquals(rules, res.getBoardRules());
        verify(userDetailsRepository).user(userName);
        verify(customUserDetailsService).build(mockUser);
    }

    @Test
    public void loadUserByUserName_UserNotFindInDataBase_CreateDefaultUser(){
        when(userDetailsRepository.user(anyString())).thenThrow(NoResultException.class);

        CustomUserDetails res = (CustomUserDetails) customUserDetailsService.loadUserByUsername("userName");

        assertNotNull(res);
        assertEquals(Roles.ROLE_ANON, res.getRole());
        assertEquals("ANON", res.getUsername());
        assertEquals("------------", res.getBoardRules());
        verify(userDetailsRepository).user("userName");
        verify(customUserDetailsService).createDefault();
    }


    @NotNull
    private Users createUser(@NotNull String userName, @NotNull String rules) {
        Board parentBoard = new Board();
        parentBoard.setName("tst");
        parentBoard.setRules(rules);

        Users res = new Users();
        res.setBoard(parentBoard);
        res.setPassword("{noop}password");
        res.setNickname(userName);
        res.setRole(false);
        return res;
    }
}
