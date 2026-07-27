package com.example.jablog.repository;

import com.example.jablog.entity.Users;
import com.example.jablog.repository.api.UserRepo;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class APIRepository {

    private final UserRepo userRepo;

    public Users login(String nickname) throws NoResultException {
        return userRepo.findUserByNickname(nickname)
                .orElseThrow(NoResultException::new);
    }
}
