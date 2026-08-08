package io.rekri.jablog.repository.api;

import io.rekri.jablog.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepoApi extends JpaRepository<Users,Long> {
    Optional<Users> findByNickname(String nickname);
}
