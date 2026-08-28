package io.rekri.jablog.unit;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import io.rekri.jablog.DTO.Login;
import io.rekri.jablog.DTO.Tokens;
import io.rekri.jablog.entity.Accounts;
import io.rekri.jablog.entity.Board;
import io.rekri.jablog.entity.Users;
import io.rekri.jablog.errors.NicknameAlreadyUsedException;
import io.rekri.jablog.repository.LoginRepository;
import io.rekri.jablog.service.JWTService;
import io.rekri.jablog.service.LoginService;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    private static final String DEFAULT_ACCOUNT_NAME = "board1";

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private LoginService loginService;

    @Test
    void extend_Success() {
        Login login = new Login();
        login.setNickname("testUser");
        login.setPassword("correctPassword");

        Board boardMock = new Board();
        Users mockUser = new Users();
        mockUser.setNickname("testUser");
        mockUser.setPassword("encodedPasswordFromDB");
        mockUser.setBoard(boardMock);

        when(loginRepository.login("testUser")).thenReturn(mockUser);
        when(passwordEncoder.matches("correctPassword", "encodedPasswordFromDB")).thenReturn(true);

        loginService.extendAccount(login, DEFAULT_ACCOUNT_NAME);

        verify(loginRepository).login("testUser");
        verify(passwordEncoder).matches("correctPassword", "encodedPasswordFromDB");
        verify(loginRepository).extendAccount(mockUser, DEFAULT_ACCOUNT_NAME);
    }

    @Test
    void extend_UserNotFound_ThrowsBadCredentialsException() {
        Login login = new Login();
        login.setNickname("unknownUser");

        when(loginRepository.login("unknownUser")).thenThrow(new NoResultException("User not found"));

        assertThrows(BadCredentialsException.class, () -> loginService.extendAccount(login, DEFAULT_ACCOUNT_NAME));

        verify(loginRepository).login("unknownUser");
        verifyNoInteractions(passwordEncoder);
        verify(loginRepository, never()).extendAccount(any(), any());
    }

    @Test
    void extend_WrongPassword_ThrowsBadCredentialsException() {
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

        assertThrows(BadCredentialsException.class, () -> loginService.extendAccount(login, DEFAULT_ACCOUNT_NAME));

        verify(loginRepository).login("testUser");
        verify(passwordEncoder).matches("incorrectPassword", "encodedPasswordFromDB");
        verify(loginRepository, never()).extendAccount(any(), any());
    }


    @Test
    void createAccount_Success() {
        Login login = new Login();
        login.setNickname("newUser");
        login.setPassword("rawPassword");

        when(loginRepository.isAccountNameAlreadyUsed("newUser")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(jwtService.generateAccessToken("newUser")).thenReturn("access-token");
        when(jwtService.generateRefreshToken("newUser")).thenReturn("refresh-token");

        Tokens result = loginService.createAccount(login);

        assertNotNull(result);
        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
        assertEquals("encodedPassword", login.getPassword());
        verify(loginRepository).createAccount(login);
    }

    @Test
    void createAccount_NicknameAlreadyUsed_ThrowsException() {
        Login login = new Login();
        login.setNickname("existingUser");
        login.setPassword("rawPassword");

        when(loginRepository.isAccountNameAlreadyUsed("existingUser")).thenReturn(true);

        assertThrows(NicknameAlreadyUsedException.class, () -> loginService.createAccount(login));

        verify(loginRepository, never()).createAccount(any());
        verifyNoInteractions(jwtService);
    }


    @Test
    void refresh_Success() {
        String refreshToken = "valid-refresh-token";
        String nickname = "testUser";
        long now = Instant.now().toEpochMilli();

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(nickname);
        when(jwtService.parseToken(refreshToken)).thenReturn(claims);

        Accounts account = new Accounts();
        account.setUsername(nickname);
        account.setRefreshExpiredTime(now + 100_000);

        when(loginRepository.findAccountByUsername(nickname)).thenReturn(Optional.of(account));
        when(jwtService.generateAccessToken(nickname)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(nickname)).thenReturn("new-refresh-token");

        Tokens result = loginService.refresh(refreshToken);

        assertNotNull(result);
        assertEquals("new-access-token", result.getAccessToken());
        assertEquals("new-refresh-token", result.getRefreshToken());
        verify(loginRepository).updateRefreshExpiredTime(eq(account), anyLong());
    }

    @Test
    void refresh_InvalidToken_ThrowsBadCredentialsException() {
        String refreshToken = "garbage-token";

        when(jwtService.parseToken(refreshToken)).thenThrow(new MalformedJwtException("bad token"));

        assertThrows(BadCredentialsException.class, () -> loginService.refresh(refreshToken));

        verify(loginRepository, never()).findAccountByUsername(any());
    }

    @Test
    void refresh_AccountNotFound_ThrowsBadCredentialsException() {
        String refreshToken = "valid-refresh-token";
        String nickname = "ghostUser";

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(nickname);
        when(jwtService.parseToken(refreshToken)).thenReturn(claims);
        when(loginRepository.findAccountByUsername(nickname)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> loginService.refresh(refreshToken));

        verify(loginRepository, never()).updateRefreshExpiredTime(any(), anyLong());
    }

    @Test
    void login_Success() {
        Login login = new Login();
        login.setNickname("testUser");
        login.setPassword("correctPassword");

        Accounts account = new Accounts();
        account.setUsername("testUser");
        account.setPassword("encodedPasswordFromDB");

        when(loginRepository.findAccountByUsername("testUser")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("correctPassword", "encodedPasswordFromDB")).thenReturn(true);
        when(jwtService.generateAccessToken("testUser")).thenReturn("access-token");
        when(jwtService.generateRefreshToken("testUser")).thenReturn("refresh-token");

        Tokens result = loginService.login(login);

        assertNotNull(result);
        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
        verify(loginRepository).updateRefreshExpiredTime(eq(account), anyLong());
    }

    @Test
    void login_UserNotFound_ThrowsIllegalArgumentException() {
        Login login = new Login();
        login.setNickname("ghostUser");
        login.setPassword("whatever");

        when(loginRepository.findAccountByUsername("ghostUser")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> loginService.login(login));

        verifyNoInteractions(jwtService);
        verify(loginRepository, never()).updateRefreshExpiredTime(any(), anyLong());
    }

    @Test
    void login_WrongPassword_ThrowsIllegalArgumentException() {
        Login login = new Login();
        login.setNickname("testUser");
        login.setPassword("wrongPassword");

        Accounts account = new Accounts();
        account.setUsername("testUser");
        account.setPassword("encodedPasswordFromDB");

        when(loginRepository.findAccountByUsername("testUser")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("wrongPassword", "encodedPasswordFromDB")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> loginService.login(login));

        verifyNoInteractions(jwtService);
        verify(loginRepository, never()).updateRefreshExpiredTime(any(), anyLong());
    }

    @Test
    void refresh_ExpiredAccordingToDbRecord_ThrowsBadCredentialsException() {
        String refreshToken = "valid-refresh-token";
        String nickname = "testUser";
        long now = Instant.now().toEpochMilli();

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(nickname);
        when(jwtService.parseToken(refreshToken)).thenReturn(claims);

        Accounts account = new Accounts();
        account.setUsername(nickname);
        account.setRefreshExpiredTime(now - 1_000); // already expired

        when(loginRepository.findAccountByUsername(nickname)).thenReturn(Optional.of(account));

        assertThrows(BadCredentialsException.class, () -> loginService.refresh(refreshToken));

        verify(jwtService, never()).generateAccessToken(any());
        verify(loginRepository, never()).updateRefreshExpiredTime(any(), anyLong());
    }
}