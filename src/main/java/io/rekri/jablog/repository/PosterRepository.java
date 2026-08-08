package io.rekri.jablog.repository;

import io.rekri.jablog.entity.Board;
import io.rekri.jablog.entity.Posts;
import io.rekri.jablog.entity.Threads;
import io.rekri.jablog.entity.Users;
import io.rekri.jablog.repository.poster.BoardRepoPoster;
import io.rekri.jablog.repository.poster.PostRepoPoster;
import io.rekri.jablog.repository.poster.ThreadRepoPoster;
import io.rekri.jablog.repository.poster.UserRepoPoster;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PosterRepository {

    private final ThreadRepoPoster threadRepoPoster;
    private final PostRepoPoster postRepoPoster;
    private final BoardRepoPoster boardRepoPoster;
    private final UserRepoPoster userRepoPoster;

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
