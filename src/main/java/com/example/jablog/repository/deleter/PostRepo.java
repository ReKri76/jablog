package com.example.jablog.repository.deleter;

import com.example.jablog.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepo extends JpaRepository<Posts, Long> {
    Posts findPostsById(long postId);
}
