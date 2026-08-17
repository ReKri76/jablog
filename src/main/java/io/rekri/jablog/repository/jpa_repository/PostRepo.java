package io.rekri.jablog.repository.jpa_repository;

import io.rekri.jablog.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepo extends JpaRepository<Posts, Long> {

    @Query("from Posts p where p.createdAt < :now - p.thread.board.lifeCyclePosts * cast(:unxtm as long)")
    List<Posts> finExpiredPots (@Param("now")long now, @Param("unxtm") long unxtm);

    @Query("select p.picture from Posts p")
    List<String> findAllPics();

    @Query("""
            select count(t)
            from Posts t
            where t.id = :postId
              and t.thread.board.name = :boardName
            """)
    Long findPosInBoard(@Param("postId") long postId, @Param("boardName") String boardName);
}
