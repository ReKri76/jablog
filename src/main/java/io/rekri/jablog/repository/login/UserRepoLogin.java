package io.rekri.jablog.repository.login;

import io.rekri.jablog.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepoLogin extends JpaRepository<Users,Long> {
    Optional<Users> findByNickname(String nickname);
}
