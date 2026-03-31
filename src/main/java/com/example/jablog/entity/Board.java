package com.example.jablog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="board")
@Getter
@Setter
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NaturalId
    @Column(name="name", nullable = false, unique = true, length = 3)
    private String name;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Threads> threads = new ArrayList<Threads>();
}
