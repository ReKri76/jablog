package io.rekri.jablog.unit;

import io.rekri.jablog.DTO.Login;
import io.rekri.jablog.entity.Board;
import io.rekri.jablog.entity.Users;
import io.rekri.jablog.repository.UsersRepository;
import io.rekri.jablog.service.UsersService;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersService usersService;

    @Captor
    private ArgumentCaptor<Users> usersCaptor;

    @Test
    void addUser_SuccessfullyAddUser() {
        String boardName = "TestBoard";
        Login login = new Login();
        login.setNickname("testUser");
        login.setPassword("rawPassword");

        Board mockBoard = new Board();
        Session mockSession = mock(Session.class);
        SimpleNaturalIdLoadAccess naturalIdLoadAccess = mock(SimpleNaturalIdLoadAccess.class);

        when(entityManager.unwrap(Session.class)).thenReturn(mockSession);
        when(mockSession.bySimpleNaturalId(Board.class)).thenReturn(naturalIdLoadAccess);
        when(naturalIdLoadAccess.getReference(boardName)).thenReturn(mockBoard);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        usersService.addUser(boardName, login);

        verify(usersRepository).addUser(usersCaptor.capture());
        Users capturedUser = usersCaptor.getValue();

        assertEquals(login.getNickname(), capturedUser.getNickname());
        assertEquals("encodedPassword", capturedUser.getPassword());
        assertEquals(mockBoard, capturedUser.getBoard());
        assertFalse(capturedUser.isRole());
    }

    @Test
    public void addUser_NicknameAlreadyUsed_ThrowsBadRequestException() {
        String boardName = "TestBoard";
        Login login = new Login();
        login.setNickname("testUser");
        login.setPassword("rawPassword");

        Board mockBoard = new Board();
        Session mockSession = mock(Session.class);
        SimpleNaturalIdLoadAccess naturalIdLoadAccess = mock(SimpleNaturalIdLoadAccess.class);

        when(entityManager.unwrap(Session.class)).thenReturn(mockSession);
        when(mockSession.bySimpleNaturalId(Board.class)).thenReturn(naturalIdLoadAccess);
        when(naturalIdLoadAccess.getReference(boardName)).thenReturn(mockBoard);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        doThrow(ConstraintViolationException.class).when(usersRepository).addUser(any(Users.class));

        assertThrows(ResponseStatusException.class, () -> usersService.addUser(boardName, login));
    }
}