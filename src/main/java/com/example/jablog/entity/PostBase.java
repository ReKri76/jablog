package com.example.jablog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@MappedSuperclass
@Getter
@Setter
public abstract class PostBase {
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
}
