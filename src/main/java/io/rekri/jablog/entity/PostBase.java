package io.rekri.jablog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class PostBase implements Comparable<PostBase>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "header", length = 200, nullable = false, updatable = false)
    private String header;

    @Column(name = "content", updatable = false)
    private String content;

    @Column(name = "picture", updatable = false)
    private String picture;

    @Column(name = "createdat", updatable = false)
    private long createdAt = System.currentTimeMillis();

    @Override
    public int compareTo(PostBase other) {
        return Long.compare(this.id, other.id);
    }
}
