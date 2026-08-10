package io.rekri.jablog.repository;

import io.rekri.jablog.entity.Posts;
import io.rekri.jablog.entity.Threads;
import io.rekri.jablog.repository.jpa_repository.PostRepo;
import io.rekri.jablog.repository.jpa_repository.ThreadRepo;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeleterRepository {

    private final PostRepo postRepoDeleter;
    private final ThreadRepo threadRepoDeleter;

    @NotNull
    public Threads thread(long threadId){

        Threads res = threadRepoDeleter.findById(threadId).orElseThrow(NoResultException::new);

        threadRepoDeleter.delete(res);

        return res;
    }

    @NotNull
    public Posts post(long postId){

        Posts res = postRepoDeleter.findById(postId).orElseThrow(NoResultException::new);

        postRepoDeleter.delete(res);

        return res;
    }
}
