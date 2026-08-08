package com.example.jablog.repository.security;

import com.example.jablog.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepo extends JpaRepository<Board, Long> {

    @Query("select b.rules from Board b where b.name = :boardName")
    String getRulesByBoardName(@Param("boardName") String boardName);
}
