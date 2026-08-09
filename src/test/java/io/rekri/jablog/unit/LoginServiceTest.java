package io.rekri.jablog.unit;

import io.rekri.jablog.DTO.Login;
import io.rekri.jablog.config.security.CustomUserDetails;
import io.rekri.jablog.config.security.Roles;
import io.rekri.jablog.entity.Board;
import io.rekri.jablog.entity.Users;
import io.rekri.jablog.repository.LoginRepository;
import io.rekri.jablog.service.CustomUserDetailsService;
import io.rekri.jablog.service.LoginService;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private LoginService loginService;

    @Test
    void login_Success() {
        Login login = new Login();
        login.setNickname("testUser");
        login.setPassword("correctPassword");

        Board boardMock = new Board();
        Users mockUser = new Users();
        mockUser.setNickname("testUser");
        mockUser.setPassword("encodedPasswordFromDB");
        mockUser.setBoard(boardMock);
        CustomUserDetails mockCustomUserDetails = new CustomUserDetails(
                "board",
                "rwdxrwdxrwdx",
                "password",
                "nickname",
                Roles.ROLE_GROUP
        );

        when(loginRepository.login("testUser")).thenReturn(mockUser);
        when(passwordEncoder.matches("correctPassword", "encodedPasswordFromDB")).thenReturn(true);
        when(customUserDetailsService.build(any(Users.class))).thenReturn(mockCustomUserDetails);

        CustomUserDetails result = loginService.login(login);

        assertNotNull(result);
        assertEquals(mockCustomUserDetails, result);
        verify(loginRepository).login("testUser");
        verify(passwordEncoder).matches("correctPassword", "encodedPasswordFromDB");
    }

    @Test
    void login_UserNotFound_ThrowsBadCredentialsException() {
        Login loginDto = new Login();
        loginDto.setNickname("unknownUser");

        when(loginRepository.login("unknownUser")).thenThrow(new NoResultException("User not found"));

        assertThrows(BadCredentialsException.class, () -> loginService.login(loginDto));

        verify(loginRepository).login("unknownUser");
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void login_WrongPassword_ThrowsBadCredentialsException() {
        Login login = new Login();
        login.setNickname("testUser");
        login.setPassword("incorrectPassword");

        Users mockUser = new Users();
        mockUser.setNickname("testUser");
        mockUser.setPassword("encodedPasswordFromDB");
        mockUser.setId(0);
        mockUser.setRole(false);
        mockUser.setBoard(null);

        when(loginRepository.login("testUser")).thenReturn(mockUser);
        when(passwordEncoder.matches("incorrectPassword", "encodedPasswordFromDB")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> loginService.login(login));

        verify(loginRepository).login("testUser");
        verify(passwordEncoder).matches("incorrectPassword", "encodedPasswordFromDB");
    }
}