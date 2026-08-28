package io.rekri.jablog.repository;

import io.rekri.jablog.entity.Board;
import io.rekri.jablog.entity.Users;
import io.rekri.jablog.repository.jpa_repository.BoardRepo;
import io.rekri.jablog.repository.jpa_repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UsersRepository {

    private final UserRepo userRepoUsers;
    private final BoardRepo boardRepo;

    @Transactional
    public void addUser(@NotNull String boardName, @NotNull String password, @NotNull String nickname){

        final Board boardRef = boardRepo.getReferenceByName(boardName);

        final Users users = new Users();
        users.setBoard(boardRef);
        users.setRole(false);
        users.setPassword(password);
        users.setNickname(nickname);

        userRepoUsers.save(users);
    }

    @Transactional
    public void deleteUser (@NotNull String nickname, @NotNull String boardName){
        userRepoUsers.deleteByUserNameAndBoardName(nickname, boardName);
    }

    @Transactional
    @NotNull
    public List<String> viewUsers(@NotNull String boardName){
        return userRepoUsers.findAllUserNamesByBoardName(boardName);
    }

    @Transactional
    @NotNull
    public List<String> getBoardsWhereThisAccountIsAdmin(@NotNull String accountName){
        return boardRepo.getBoardsWhereThisAccountIsAdmin(accountName);
    }

    @Transactional
    public boolean isUserNameAlreadyUsed(@NotNull String accountName){
        Optional<Users> accounts = userRepoUsers.findByNickname(accountName);
        return accounts.isPresent();
    }
}
