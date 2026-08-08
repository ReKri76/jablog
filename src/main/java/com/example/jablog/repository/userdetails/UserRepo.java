package com.example.jablog.repository.userdetails;

import com.example.jablog.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<Users, Long> {

    Optional<Users> findByNickname(String nickname);
}
