package com.example.jablog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="posts")
@Getter
@Setter
public class Posts extends PostBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="thread")
    private Threads thread;

}
