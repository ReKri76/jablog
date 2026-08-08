package com.example.jablog.repository;

import com.example.jablog.entity.Users;
import com.example.jablog.repository.userdetails.UserRepo;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserDetailsRepository {

    private final UserRepo userRepo;

    @NotNull
    @Transactional
    public Users user (@NotNull String username) throws NoResultException {
        return userRepo.findByNickname(username).orElseThrow(NoResultException::new);
    }
}
