package com.example.jablog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="users")
@Getter
@Setter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name =  "role", nullable = false) //check
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board", referencedColumnName = "name")
    private String board;
}
