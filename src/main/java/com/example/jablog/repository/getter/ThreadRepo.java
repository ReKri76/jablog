package com.example.jablog.repository.getter;

import com.example.jablog.entity.Threads;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ThreadRepo extends JpaRepository<Threads, Long> {
    @Query("from Threads t left join fetch t.posts where t.id = :threadId")
    Optional<Threads> findThreadsByIdWithPosts(@Param("threadID")long threadId);

    @Query("from Threads t where t.board.name = :boardName")
    List<Threads> findThreadsByBoardNameByPageable(@Param("boardName") String boardName, Pageable pageable);
}
