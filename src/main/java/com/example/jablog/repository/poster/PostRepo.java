package com.example.jablog.repository.poster;

import com.example.jablog.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepo extends JpaRepository<Posts, Long> {
}
