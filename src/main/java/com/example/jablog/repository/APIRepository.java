package com.example.jablog.repository;

import com.example.jablog.entity.Users;
import com.example.jablog.repository.api.ThreadRepo;
import com.example.jablog.repository.api.UserRepo;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class APIRepository {

    private final UserRepo userRepo;
    private final ThreadRepo threadRepo;

    public Users login(String nickname) throws NoResultException {
        return userRepo.findByNickname(nickname)
                .orElseThrow(NoResultException::new);
    }

    public void likeThread(int threadId){
        threadRepo.likeThread(threadId);
    }

    public void dislikeThread(int threadId){
        threadRepo.dislikeThread(threadId);
    }
}
