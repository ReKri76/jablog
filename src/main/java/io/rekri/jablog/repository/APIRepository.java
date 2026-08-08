package io.rekri.jablog.repository;

import io.rekri.jablog.entity.Users;
import io.rekri.jablog.repository.api.ThreadRepoApi;
import io.rekri.jablog.repository.api.UserRepoApi;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class APIRepository {

    private final UserRepoApi userRepoApi;
    private final ThreadRepoApi threadRepoApi;

    public Users login(String nickname) throws NoResultException {
        return userRepoApi.findByNickname(nickname)
                .orElseThrow(NoResultException::new);
    }

    public void likeThread(int threadId){
        threadRepoApi.likeThread(threadId);
    }

    public void dislikeThread(int threadId){
        threadRepoApi.dislikeThread(threadId);
    }
}