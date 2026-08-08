package com.example.jablog.repository.security;

import com.example.jablog.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepo extends JpaRepository<Posts, Long> {

    @Query("""
            select count(t)
            from Posts t
            where t.id = :postId
              and t.thread.board.name = :boardName
            """)
    Long findPosInBoard(@Param("postId") long postId, @Param("boardName") String boardName);
}
