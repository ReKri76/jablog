package io.rekri.jablog.repository.security;

import io.rekri.jablog.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepoSecurity extends JpaRepository<Board, Long> {

    @Query("select b.rules from Board b where b.name = :boardName")
    String getRulesByBoardName(@Param("boardName") String boardName);
}
