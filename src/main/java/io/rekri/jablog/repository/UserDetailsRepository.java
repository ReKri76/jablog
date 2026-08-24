package io.rekri.jablog.repository;

import io.rekri.jablog.entity.Users;
import io.rekri.jablog.repository.jpa_repository.UserRepo;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDetailsRepository {

    private final UserRepo userRepoUserDetails;

    @NotNull
    @Transactional
    public Users user (@NotNull String username) throws NoResultException {
        return userRepoUserDetails.findByNickname(username).orElseThrow(NoResultException::new);
    }

    @NotNull
    @Transactional
    public Optional<Users> getUserByAccountAndBoard(@NotNull String accountName, @NotNull String boardName){
        return userRepoUserDetails.findByAccountNameAndBoard(boardName, accountName);
    }
}
