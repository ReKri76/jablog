package com.example.jablog.repository.cleaner;

import com.example.jablog.entity.Threads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ThreadRepo extends JpaRepository<Threads,Long> {

    @Query("from Threads t " +
            "where not exists(" +
            "select 1 from Posts p " +
            "where p.createdAt >= :now - t.board.lifeCycleThreads * :unxtm " +
            "and p.thread = t" +
            ") " +
            "and t.createdAt < :oldThread")
    List<Threads> findExpiresThreads(long now, long unxtm, long oldThread);

    @Query("select t.picture from Threads t")
    List<String> findAllPics();
}

