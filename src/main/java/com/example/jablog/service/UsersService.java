package com.example.jablog.service;

import com.example.jablog.entity.Board;
import com.example.jablog.entity.Users;
import com.example.jablog.repository.UsersRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;

    @Transactional
    public void addUser(String boardName, String nickname, String password){

        final Board boardRef = entityManager.unwrap(Session.class)
                .bySimpleNaturalId(Board.class)
                .getReference(boardName);
        final Users users = new Users();
        users.setBoard(boardRef);
        users.setRole(false);
        users.setPassword(passwordEncoder.encode(password));
        users.setNickname(nickname);

        usersRepository.addUser(users);
    }

    @Transactional
    public void deleteUser(String nickname){
        usersRepository.deleteUser(nickname);
    }

    @Transactional
    public ArrayList<String> viewUsers(String boardName){
        return usersRepository.viewUsers(boardName);
    }
}
