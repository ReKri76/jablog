package io.rekri.jablog.entity;

import jakarta.persistence.*;
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
