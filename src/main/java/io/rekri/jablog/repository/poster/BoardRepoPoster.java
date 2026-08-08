package io.rekri.jablog.repository.poster;

import io.rekri.jablog.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepoPoster extends JpaRepository<Board, Long> {
}
