package com.example.jablog.repository.cleaner;

import com.example.jablog.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepo extends JpaRepository<Board,Long> {

    @Query("from Board b left join fetch b.users where not exists(select 1 from Threads t where t.board = b) " +
            "and b.createdAt < :oldBoard")
    List<Board> findExpiresBoards(long oldBoard);
}
