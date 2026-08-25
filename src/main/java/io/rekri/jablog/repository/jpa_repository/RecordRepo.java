package io.rekri.jablog.repository.jpa_repository;

import io.rekri.jablog.entity.Records;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepo extends JpaRepository<Records, Long> {
    void createByAccountNameAndUserName();
}
