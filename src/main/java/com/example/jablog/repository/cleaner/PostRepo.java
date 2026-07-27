package com.example.jablog.repository.cleaner;

import com.example.jablog.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepo extends JpaRepository<Posts, Long> {

    @Query("from Posts p where p.createdAt < :now - p.thread.board.lifeCyclePosts * :unxtm")
    List<Posts> finExpiredPots (long now, long unxtm);

    @Query("select p.picture from Posts p")
    List<String> findAllPics();
}
