package com.example.jablog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name="threads")
@Getter
@Setter
public class Threads extends PostBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board", referencedColumnName = "name")
    private Board board;

    @OneToMany(mappedBy = "thread", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Posts> posts = new TreeSet<Posts>();
}
