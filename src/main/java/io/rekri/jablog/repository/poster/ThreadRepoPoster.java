package io.rekri.jablog.repository.poster;

import io.rekri.jablog.entity.Threads;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadRepoPoster extends JpaRepository<Threads, Long> {
}
