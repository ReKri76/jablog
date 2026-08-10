package io.rekri.jablog.repository;

import io.rekri.jablog.entity.Board;
import io.rekri.jablog.entity.Posts;
import io.rekri.jablog.entity.Threads;
import io.rekri.jablog.entity.Users;
import io.rekri.jablog.repository.jpa_repository.BoardRepo;
import io.rekri.jablog.repository.jpa_repository.PostRepo;
import io.rekri.jablog.repository.jpa_repository.ThreadRepo;
import io.rekri.jablog.repository.jpa_repository.UserRepo;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PosterRepository {

    private final ThreadRepo threadRepoPoster;
    private final PostRepo postRepoPoster;
    private final BoardRepo boardRepoPoster;
    private final UserRepo userRepoPoster;

    @Transactional
    public long thread(@NotNull Threads threads) {
        threadRepoPoster.save(threads);
        return threads.getId();
    }

    @Transactional
    public void post(@NotNull Posts posts) {
        postRepoPoster.save(posts);}

    @Transactional
    public void board(@NotNull Board board, @NotNull Users users){
        boardRepoPoster.save(board);
        userRepoPoster.save(users);
    }

    @NotNull
    @Transactional
    public Threads getThreadsById(long id){
        return threadRepoPoster.findById(id).orElseThrow(NoResultException::new);
     }
}
