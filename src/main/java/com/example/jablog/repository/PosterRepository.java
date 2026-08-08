package com.example.jablog.repository;

import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.entity.Users;
import com.example.jablog.repository.poster.BoardRepo;
import com.example.jablog.repository.poster.PostRepo;
import com.example.jablog.repository.poster.ThreadRepo;
import com.example.jablog.repository.poster.UserRepo;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PosterRepository {

    private final ThreadRepo threadRepo;
    private final PostRepo postRepo;
    private final BoardRepo boardRepo;
    private final UserRepo userRepo;

    @Transactional
    public long thread(@NotNull Threads threads) {
        threadRepo.save(threads);
        return threads.getId();
    }

    @Transactional
    public void post(@NotNull Posts posts) {postRepo.save(posts);}

    @Transactional
    public void board(@NotNull Board board, @NotNull Users users){
        boardRepo.save(board);
        userRepo.save(users);
    }

    @NotNull
    @Transactional
    public Threads getThreadsById(long id){
        return threadRepo.findById(id).orElseThrow(NoResultException::new);
     }
}
