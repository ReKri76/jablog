package io.rekri.jablog.repository;

import io.rekri.jablog.entity.Users;
import io.rekri.jablog.repository.jpa_repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UsersRepository {

    private final UserRepo userRepoUsers;

    public void addUser(@NotNull Users users){
        userRepoUsers.save(users);
    }

    public void deleteUser (@NotNull String nickname, @NotNull String boardName){
        userRepoUsers.deleteByUserNameAndBoardName(nickname, boardName);
    }

    @NotNull
    public List<String> viewUsers(@NotNull String boardName){
        return userRepoUsers.findAllUserNamesByBoardName(boardName);
    }
}
