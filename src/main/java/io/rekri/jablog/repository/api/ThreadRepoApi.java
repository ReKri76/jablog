package io.rekri.jablog.repository.api;

import io.rekri.jablog.entity.Threads;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThreadRepoApi extends JpaRepository<Threads, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Threads t SET t.carma = t.carma + 1 WHERE t.id = :threadId")
    void likeThread(@Param("threadId") int threadId);

    @Modifying
    @Transactional
    @Query("UPDATE Threads t SET t.carma = t.carma - 1 WHERE t.id = :threadId")
    void dislikeThread(@Param("threadId") int threadId);
}
