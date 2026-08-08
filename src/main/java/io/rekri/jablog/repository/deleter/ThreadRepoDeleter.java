package io.rekri.jablog.repository.deleter;

import io.rekri.jablog.entity.Threads;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadRepoDeleter extends JpaRepository<Threads, Long> {
}
