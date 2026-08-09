package io.rekri.jablog.repository.getter;

import io.rekri.jablog.entity.Threads;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ThreadRepoGetter extends JpaRepository<Threads, Long> {
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
}
