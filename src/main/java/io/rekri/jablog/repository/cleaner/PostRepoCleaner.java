package io.rekri.jablog.repository.cleaner;

import io.rekri.jablog.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepoCleaner extends JpaRepository<Posts, Long> {

    @Query("from Posts p where p.createdAt < :now - p.thread.board.lifeCyclePosts * :unxtm")
    List<Posts> finExpiredPots (@Param("now")long now, @Param("unxtm") long unxtm);

    @Query("select p.picture from Posts p")
    List<String> findAllPics();
}
