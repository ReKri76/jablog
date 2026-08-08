package io.rekri.jablog.service;

import io.rekri.jablog.DTO.Login;
import io.rekri.jablog.entity.Board;
import io.rekri.jablog.entity.Users;
import io.rekri.jablog.repository.UsersRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersService {

    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;

    @Transactional
    public void addUser(@NotNull String boardName, @NotNull Login login){

        log.info("Start add user.");

        final String password = login.getPassword();
        final String nickname = login.getNickname();

        final Board boardRef = entityManager.unwrap(Session.class)
                .bySimpleNaturalId(Board.class)
                .getReference(boardName);
        final Users users = new Users();
        users.setBoard(boardRef);
        users.setRole(false);
        users.setPassword(passwordEncoder.encode(password));
        users.setNickname(nickname);

        try {
            usersRepository.addUser(users);
        } catch(ConstraintViolationException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недопустимые значения создания пользователя.\n" +
                    "Вероятнее всего пользователь с таким никнеймом уже существует, либо никнейм слишком длинный.");
        }

        log.info("User {} was added.", users.getNickname());
    }

    @Transactional
    public void deleteUser(@NotNull String nickname, @NotNull String boardName){
        log.info("Start deleting user {}", nickname);
        usersRepository.deleteUser(nickname, boardName);
        log.info("User {} was deleted", nickname);
    }

    @Transactional
    @NotNull
    public List<String> viewUsers(@NotNull String boardName){
        return usersRepository.viewUsers(boardName);
    }
}
