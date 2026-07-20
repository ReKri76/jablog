package com.example.jablog.unit;

import com.example.jablog.DTO.Login;
import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.config.security.Roles;
import com.example.jablog.entity.Board;
import com.example.jablog.entity.Users;
import com.example.jablog.repository.APIRepository;
import com.example.jablog.service.APIService;
import com.example.jablog.service.CustomUserDetailsService;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class APIServiceTest {

    @Mock
    private APIRepository apiRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private APIService apiService;

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

        when(apiRepository.login("testUser")).thenReturn(mockUser);
        when(passwordEncoder.matches("correctPassword", "encodedPasswordFromDB")).thenReturn(true);
        when(customUserDetailsService.build(any(Users.class))).thenReturn(mockCustomUserDetails);

        CustomUserDetails result = apiService.login(login);

        assertNotNull(result);
        assertEquals(mockCustomUserDetails, result);
        verify(apiRepository).login("testUser");
        verify(passwordEncoder).matches("correctPassword", "encodedPasswordFromDB");
    }

    @Test
    void login_UserNotFound_ThrowsNoResultException() {
        Login loginDto = new Login();
        loginDto.setNickname("unknownUser");

        when(apiRepository.login("unknownUser")).thenThrow(new NoResultException("User not found"));

        assertThrows(NoResultException.class, () -> apiService.login(loginDto));

        verify(apiRepository).login("unknownUser");
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void login_WrongPassword_ThrowsNoResultException() {
        Login login = new Login();
        login.setNickname("testUser");
        login.setPassword("incorrectPassword");

        Users mockUser = new Users();
        mockUser.setNickname("testUser");
        mockUser.setPassword("encodedPasswordFromDB");
        mockUser.setId(0);
        mockUser.setRole(false);
        mockUser.setBoard(null);

        when(apiRepository.login("testUser")).thenReturn(mockUser);
        when(passwordEncoder.matches("incorrectPassword", "encodedPasswordFromDB")).thenReturn(false);

        assertThrows(NoResultException.class, () -> apiService.login(login));

        verify(apiRepository).login("testUser");
        verify(passwordEncoder).matches("incorrectPassword", "encodedPasswordFromDB");
    }
}