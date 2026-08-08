package com.example.jablog.repository;

import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.repository.deleter.PostRepo;
import com.example.jablog.repository.deleter.ThreadRepo;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeleterRepository {

    private final PostRepo postRepo;
    private final ThreadRepo threadRepo;

    @NotNull
    public Threads thread(long threadId){

        Threads res = threadRepo.findById(threadId).orElseThrow(NoResultException::new);

        threadRepo.delete(res);

        return res;
    }

    @NotNull
    public Posts post(long postId){

        Posts res = postRepo.findById(postId).orElseThrow(NoResultException::new);

        postRepo.delete(res);

        return res;
    }
}
