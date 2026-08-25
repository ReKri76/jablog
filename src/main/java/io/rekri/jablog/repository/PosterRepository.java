package io.rekri.jablog.repository;

import io.rekri.jablog.DTO.BoardToCreate;
import io.rekri.jablog.entity.*;
import io.rekri.jablog.repository.jpa_repository.*;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PosterRepository {

    private final ThreadRepo threadRepoPoster;
    private final PostRepo postRepoPoster;
    private final BoardRepo boardRepoPoster;
    private final UserRepo userRepoPoster;
    private final RecordRepo recordRepo;
    private final AccountRepo accountRepo;

    @Transactional
    public long thread(@NotNull String body, @NotNull String head, @NotNull String picture, @NotNull String boardName) {

        final Board boardRef = boardRepoPoster.getReferenceByName(boardName);

        final Threads threads = new Threads();
        threads.setContent(body);
        threads.setHeader(head);
        threads.setCarma(0);
        threads.setPicture(picture);
        threads.setBoard(boardRef);

        threadRepoPoster.save(threads);
        return threads.getId();
    }

    @Transactional
    public void post(@NotNull String body, @NotNull String head, @NotNull String picture, long threadId) {

        Threads threads = threadRepoPoster.findById(threadId).orElseThrow(NoResultException::new);

        final Posts posts = new Posts();
        posts.setContent(body);
        posts.setHeader(head);
        posts.setThread(threads);
        posts.setPicture(picture);

        postRepoPoster.save(posts);
    }

    @Transactional
    public void board(@NotNull BoardToCreate boardToCreate, @NotNull String password, @Nullable String accountName){

        final Board board = new Board();
        board.setName(boardToCreate.getBoardName());
        board.setRules(boardToCreate.getRule());
        board.setLifeCyclePosts(boardToCreate.getLifeCyclePosts());
        board.setLifeCycleThreads(boardToCreate.getLifeCycleThreads());
        board.setTranscription(boardToCreate.getTranscription());

        final Users users = new Users();
        users.setBoard(board);
        users.setRole(true);
        users.setPassword(password);
        users.setNickname(boardToCreate.getNickname());

        boardRepoPoster.save(board);
        userRepoPoster.save(users);

        if (accountName!=null){
            final Records records = new Records();
            records.setUser(users);

            final Accounts accounts = accountRepo.getReferenceByUsername(accountName);
            records.setAccount(accounts);

            recordRepo.save(records);
        }
    }
}
