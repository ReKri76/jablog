package io.rekri.jablog.repository.jpa_repository;

import io.rekri.jablog.entity.Threads;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ThreadRepo extends JpaRepository<Threads,Long> {

    @Query("from Threads t " +
            "where not exists(" +
            "select 1 from Posts p " +
            "where p.createdAt >= :now - t.board.lifeCycleThreads * cast(:unxtm as long) " +
            "and p.thread = t" +
            ") " +
            "and t.createdAt < :oldThread")
    List<Threads> findExpiresThreads(@Param("now")long now, @Param("unxtm") long unxtm, @Param("oldThread")long oldThread);

    @Query("select t.picture from Threads t")
    List<String> findAllPics();

    @Query("from Threads t left join fetch t.posts where t.id = :threadId")
    Optional<Threads> findThreadsByIdWithPosts(@Param("threadId")long threadId);

    @Query("from Threads t where t.board.name = :boardName")
    List<Threads> findThreadsByBoardNameByPageable(@Param("boardName") String boardName, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Threads t SET t.carma = t.carma + 1 WHERE t.id = :threadId")
    void likeThread(@Param("threadId") int threadId);

    @Modifying
    @Transactional
    @Query("UPDATE Threads t SET t.carma = t.carma - 1 WHERE t.id = :threadId")
    void dislikeThread(@Param("threadId") int threadId);

    @Query("""
            select count(t)
            from Threads t
            where t.id = :threadId
              and t.board.name = :boardName
            """)
    Long findThreadInBoard(@Param("threadId") long threadId, @Param("boardName") String boardName);
}

