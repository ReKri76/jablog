package io.rekri.jablog.repository.poster;

import io.rekri.jablog.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepoPoster extends JpaRepository<Posts, Long> {
}
