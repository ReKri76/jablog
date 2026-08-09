package io.rekri.jablog.repository;

import io.rekri.jablog.entity.Users;
import io.rekri.jablog.repository.login.UserRepoLogin;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LoginRepository {

    private final UserRepoLogin userRepoLogin;

    public Users login(String nickname) throws NoResultException {
        return userRepoLogin.findByNickname(nickname)
                .orElseThrow(NoResultException::new);
    }
}