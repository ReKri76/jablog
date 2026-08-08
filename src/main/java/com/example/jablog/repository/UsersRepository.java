package com.example.jablog.repository;

import com.example.jablog.entity.Users;
import com.example.jablog.repository.users.UserRepo;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UsersRepository {

    private final EntityManager entityManager;
    private final UserRepo userRepo;

    public void addUser(@NotNull Users users){
        userRepo.save(users);
    }

    public void deleteUser (@NotNull String nickname, @NotNull String boardName){
        userRepo.deleteByUserNameAndBoardName(nickname, boardName);
    }

    @NotNull
    public List<String> viewUsers(@NotNull String boardName){
        return userRepo.findAllUserNamesByBoardName(boardName);
    }
}
