package io.rekri.jablog.repository;

import io.rekri.jablog.entity.Board;
import io.rekri.jablog.entity.Posts;
import io.rekri.jablog.entity.Threads;
import io.rekri.jablog.repository.cleaner.BoardRepoCleaner;
import io.rekri.jablog.repository.cleaner.PostRepoCleaner;
import io.rekri.jablog.repository.cleaner.ThreadRepoCleaner;
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

    private final PostRepoCleaner postRepo;
    private final ThreadRepoCleaner threadRepoCleaner;
    private final BoardRepoCleaner boardRepoCleaner;

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


        final List<Threads> threads = threadRepoCleaner.findExpiresThreads(now, UNIX_TIME_DAY, oldThread);

        threadRepoCleaner.deleteAll(threads);

        return threads;
    }

    @Transactional
    public List<String> pics(){

        final List<String> pics = threadRepoCleaner.findAllPics();
        pics.addAll(postRepo.findAllPics());

        return pics;
    }

    @Transactional
    public List<Board> boards(long oldBoard){

        final List<Board> boards = boardRepoCleaner.findExpiresBoards(oldBoard);

        boardRepoCleaner.deleteAll(boards);

        return boards;
    }
}
