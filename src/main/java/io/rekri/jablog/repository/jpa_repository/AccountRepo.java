package io.rekri.jablog.repository.jpa_repository;

import io.rekri.jablog.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepo extends JpaRepository<Accounts, Long> {
    Accounts getReferenceByUsername(String name);
    Optional<Accounts> findByUsername(String name);

    @Query("""
            select a
            from Accounts a
            left join fetch Records
            where a.refreshExpiredTime < :expiredTime and a.records.size=0
            """)
    List<Accounts> findExpiredAccounts(@Param("expiredTime") long expiredTime);
}
