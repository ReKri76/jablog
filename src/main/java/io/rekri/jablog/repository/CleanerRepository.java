package io.rekri.jablog.repository;

import io.rekri.jablog.entity.Accounts;
import io.rekri.jablog.entity.Board;
import io.rekri.jablog.entity.Posts;
import io.rekri.jablog.entity.Threads;
import io.rekri.jablog.repository.jpa_repository.AccountRepo;
import io.rekri.jablog.repository.jpa_repository.BoardRepo;
import io.rekri.jablog.repository.jpa_repository.PostRepo;
import io.rekri.jablog.repository.jpa_repository.ThreadRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CleanerRepository {

    private static final long UNIX_TIME_DAY = Duration.ofDays(1).toMillis();

    private final PostRepo postRepo;
    private final ThreadRepo threadRepo;
    private final BoardRepo boardRepo;
    private final AccountRepo accountRepo;

    @Transactional
    public List<Posts> posts() {
        final long now = Instant.now().toEpochMilli();

        final List<Posts> posts = postRepo.finExpiredPots(now, UNIX_TIME_DAY);

        postRepo.deleteAll(posts);

        return posts;
    }

    @Transactional
    public List<Threads> threads(long oldThread){
        final long now = Instant.now().toEpochMilli();

        final List<Threads> threads = threadRepo.findExpiresThreads(now, UNIX_TIME_DAY, oldThread);

        threadRepo.deleteAll(threads);

        return threads;
    }

    @Transactional
    public List<String> pics(){

        final List<String> pics = threadRepo.findAllPics();
        pics.addAll(postRepo.findAllPics());

        return pics;
    }

    @Transactional
    public List<Board> boards(long oldBoard){

        final List<Board> boards = boardRepo.findExpiresBoards(oldBoard);

        boardRepo.deleteAll(boards);

        return boards;
    }

    @Transactional
    public List<Accounts> accounts(long oldAccount){
        final List<Accounts> res = accountRepo.findExpiredAccounts(oldAccount);

        accountRepo.deleteAll(res);

        return res;
    }
}
