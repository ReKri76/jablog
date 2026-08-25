package io.rekri.jablog.repository.jpa_repository;

import io.rekri.jablog.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepo extends JpaRepository<Accounts, Long> {
    Accounts getReferenceByUsername(String name);
}
