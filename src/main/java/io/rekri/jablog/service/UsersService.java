package io.rekri.jablog.service;

import io.rekri.jablog.DTO.Login;
import io.rekri.jablog.errors.NicknameAlreadyUsedException;
import io.rekri.jablog.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersService {

    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;

    @Transactional
    public void addUser(@NotNull String boardName, @NotNull Login login){

        log.info("Start add user.");

        String password = Objects.requireNonNull(passwordEncoder.encode(login.getPassword()));

        try {
            usersRepository.addUser(boardName, login.getNickname(), password);
        } catch(ConstraintViolationException e){
            throw new NicknameAlreadyUsedException("Недопустимые значения создания пользователя.\n" +
                    "Вероятнее всего пользователь с таким никнеймом уже существует, либо никнейм слишком длинный.");
        }

        log.info("User {} was added.", login.getNickname());
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

    @Transactional
    @NotNull
    public List<String> getBoardsWhereThisAccountIsAdmin(@NotNull String accountName){
        return usersRepository.getBoardsWhereThisAccountIsAdmin(accountName);
    }
}
