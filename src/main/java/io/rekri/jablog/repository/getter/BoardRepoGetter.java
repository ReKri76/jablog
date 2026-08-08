package io.rekri.jablog.repository.getter;

import io.rekri.jablog.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepoGetter extends JpaRepository<Board, Long> {
}
