package com.example.jablog.repository.poster;

import com.example.jablog.entity.Threads;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadRepo extends JpaRepository<Threads, Long> {
}
