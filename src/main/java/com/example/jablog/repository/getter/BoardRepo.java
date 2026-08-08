package com.example.jablog.repository.getter;

import com.example.jablog.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepo extends JpaRepository<Board, Long> {
}
