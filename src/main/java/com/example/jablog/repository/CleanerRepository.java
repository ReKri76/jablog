package com.example.jablog.repository;

import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.repository.cleaner.BoardRepo;
import com.example.jablog.repository.cleaner.PostRepo;
import com.example.jablog.repository.cleaner.ThreadRepo;
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
}
