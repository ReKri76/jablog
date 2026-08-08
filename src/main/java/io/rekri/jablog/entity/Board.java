package io.rekri.jablog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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

    public static final int NUMBER_OF_RULES_GROUPS = 3;
    public static final int SIZE_OF_GROUP = 4;
    public static final int SIZE_OF_ARRAY_OF_RULES = SIZE_OF_GROUP * NUMBER_OF_RULES_GROUPS;
    public static final int MAX_LIFE_CYCLE_OF_THREADS = 28;

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
     * rwdx - full access
     * rwd- - read threads, read posts, create posts, delete posts
     * rw-x - read threads, read posts, create posts, create threads
     * rw-- - read threads, read posts, create posts
     * r-dx - rad thread, read posts, delete posts, delete threads
     * r-d- - rad thread, read posts, delete posts
     * r--x - read threads, read boards
     * r--- - read threads
     * -wdx - incorrect
     * -wd- - incorrect
     * -w-x - incorrect
     * -w-- - incorrect
     * --dx - incorrect
     * --d- - incorrect
     * ---x - incorrect
     * ---- - have no access
     * (owner), (group), (other);
     * */
    @Column(name="rules", nullable = false, length = 12)
    @Pattern(regexp = "^(----|r[w-][d-][x-]){3}$")
    private String rules = "rw-xrw-xrw-x";

    @Column(name="lifeCycleThreads",  nullable = false)
    @Max(MAX_LIFE_CYCLE_OF_THREADS)
    @Min(2)
    private int lifeCycleThreads = 14;

    @Column(name="lifeCyclePosts", nullable = false,
            check = @CheckConstraint(constraint = "life_cycle_posts<life_cycle_threads"))
    @Min(1)
    private int lifeCyclePosts = 7;

    @Column(name="createdat", nullable = false, updatable = false)
    private long createdAt = System.currentTimeMillis();

    @Column(name="transcription", nullable = false, updatable = false)
    private String transcription;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Threads> threads = new HashSet<Threads>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Users> users = new HashSet<Users>();
}
