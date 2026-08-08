package io.rekri.jablog.repository.security;

import io.rekri.jablog.entity.Threads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThreadRepoSecurity extends JpaRepository<Threads, Long> {
    @Query("""
            select count(t)
            from Threads t
            where t.id = :threadId
              and t.board.name = :boardName
            """)
    Long findThreadInBoard(@Param("threadId") long threadId, @Param("boardName") String boardName);
}
