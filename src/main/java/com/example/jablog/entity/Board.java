package com.example.jablog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.HashSet;
import java.util.Set;

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

    @Column(name="rules", nullable = false,
            check = @CheckConstraint(constraint = "rules <@ ARRAY['r', 'w', '-']::char(1)[]"))
    @Size(min = 6, max = 6)
    private String[] rules = {"r","w","r","w","r","w"};

    @Column(name="lifeCycleThreads",  nullable = false)
    @Max(28)
    @Min(0)
    private int lifeCycleThreads = 14;

    @Column(name="lifeCyclePosts", nullable = false,
            check = @CheckConstraint(constraint = "life_cycle_posts<life_cycle_threads"))
    @Min(1)
    private int lifeCyclePosts = 7;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Threads> threads = new HashSet<Threads>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Users> users = new HashSet<Users>();
}
