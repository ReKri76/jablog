package io.rekri.jablog.repository.deleter;

import io.rekri.jablog.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepoDeleter extends JpaRepository<Posts, Long> { }
