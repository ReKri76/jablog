package io.rekri.jablog.repository;

import io.rekri.jablog.entity.Users;
import io.rekri.jablog.repository.jpa_repository.UserRepo;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LoginRepository {

    private final UserRepo userRepo;

    public Users login(String nickname) throws NoResultException {
        return userRepo.findByNickname(nickname)
                .orElseThrow(NoResultException::new);
    }
}