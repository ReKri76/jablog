package com.example.jablog.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="threads")
@Getter
@Setter
public class Threads extends PostBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board", referencedColumnName = "name")
    private Board board;

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Posts> threads = new HashSet<Posts>();
}
