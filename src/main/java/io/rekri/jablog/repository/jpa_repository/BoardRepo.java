package io.rekri.jablog.repository.jpa_repository;

import io.rekri.jablog.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepo extends JpaRepository<Board,Long> {

    @Query("from Board b left join fetch b.users where not exists(select 1 from Threads t where t.board = b) " +
            "and b.createdAt < :oldBoard")
    List<Board> findExpiresBoards(@Param("oldBoard")long oldBoard);

    @Query("select b.rules from Board b where b.name = :boardName")
    String getRulesByBoardName(@Param("boardName") String boardName);

    Board getReferenceByName(String boardName);

    @Query("""
        select b.name
        from Board b
        where exists (
                select 1 from Records r where(
                        r.user.board = b and r.account.username = :accountName and r.user.role
                        )
        )
        """)
    List<String> getBoardsWhereThisAccountIsAdmin(@Param("accountName") String accountName);
}
