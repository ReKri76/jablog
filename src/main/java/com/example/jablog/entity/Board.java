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
    @Column(name="name", nullable = false, unique = true, length = 3, updatable = false)
    private String name;

    /**
     * r - read board;
     * w - write posts;
     * d - can delete;
     * x - have access to threads;
     * no one can have access to threads and havent to read board or write boards
     * users with 'x' modificator also can delete threads if they have 'd' modificator;
     * (owner), (group), (other);
     * */
    @Column(name="rules", nullable = false,
            check = @CheckConstraint(constraint = "rules <@ ARRAY['r', 'w', 'd', 'x', '-']"))
    @Size(min = 12, max = 12)
    private String[] rules = {"r","w","-","x", "r","w","-","x", "r","w","-","x"};

    @Column(name="lifeCycleThreads",  nullable = false)
    @Max(28)
    @Min(2)
    private int lifeCycleThreads = 14;

    @Column(name="lifeCyclePosts", nullable = false,
            check = @CheckConstraint(constraint = "life_cycle_posts<life_cycle_threads"))
    @Min(1)
    private int lifeCyclePosts = 7;

    @Column(name="createdat", nullable = false, updatable = false)
    private long createdAt = System.currentTimeMillis();

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Threads> threads = new HashSet<Threads>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Users> users = new HashSet<Users>();
}
