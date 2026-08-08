package io.rekri.jablog.repository.poster;

import io.rekri.jablog.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepoPoster extends JpaRepository<Users, Long> {
}
