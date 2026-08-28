package io.rekri.jablog.unit;

import io.rekri.jablog.DTO.Login;
import io.rekri.jablog.errors.NicknameAlreadyUsedException;
import io.rekri.jablog.repository.UsersRepository;
import io.rekri.jablog.service.UsersService;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersService usersService;

    @Test
    void addUser_SuccessfullyAddUser() {
        String boardName = "TestBoard";
        Login login = new Login();
        login.setNickname("testUser");
        login.setPassword("rawPassword");

        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        usersService.addUser(boardName, login);

        verify(usersRepository).addUser(boardName, "testUser", "encodedPassword");
    }

    @Test
    void addUser_NicknameAlreadyUsed_ThrowsBadRequestException() {
        String boardName = "TestBoard";
        Login login = new Login();
        login.setNickname("testUser");
        login.setPassword("rawPassword");

        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        doThrow(ConstraintViolationException.class)
                .when(usersRepository).addUser(boardName, "testUser", "encodedPassword");

        assertThrows(NicknameAlreadyUsedException.class, () -> usersService.addUser(boardName, login));
    }

    @Test
    void deleteUser_CallsRepositoryWithCorrectArgs() {
        String nickname = "testUser";
        String boardName = "TestBoard";

        usersService.deleteUser(nickname, boardName);

        verify(usersRepository).deleteUser(nickname, boardName);
    }

    @Test
    void viewUsers_ReturnsListFromRepository() {
        String boardName = "TestBoard";
        List<String> users = List.of("n1", "n2", "n3");
        when(usersRepository.viewUsers(boardName)).thenReturn(users);

        List<String> result = usersService.viewUsers(boardName);

        assertEquals(users, result);
        verify(usersRepository).viewUsers(boardName);
    }

    @Test
    void getBoardsWhereThisAccountIsAdmin_ReturnsListFromRepository() {
        String accountName = "admin";
        List<String> boards = List.of("dev", "qa");
        when(usersRepository.getBoardsWhereThisAccountIsAdmin(accountName)).thenReturn(boards);

        List<String> result = usersService.getBoardsWhereThisAccountIsAdmin(accountName);

        assertEquals(boards, result);
        verify(usersRepository).getBoardsWhereThisAccountIsAdmin(accountName);
    }
}