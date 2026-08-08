package io.rekri.jablog.repository;

import io.rekri.jablog.entity.Users;
import io.rekri.jablog.repository.userdetails.UserRepoUserDetails;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserDetailsRepository {

    private final UserRepoUserDetails userRepoUserDetails;

    @NotNull
    @Transactional
    public Users user (@NotNull String username) throws NoResultException {
        return userRepoUserDetails.findByNickname(username).orElseThrow(NoResultException::new);
    }
}
