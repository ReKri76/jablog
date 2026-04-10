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

    @Column(name="nickname",  nullable=false, unique=true, length = 10)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name =  "role", nullable = false)
    private boolean role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board", referencedColumnName = "name")
    private Board board;
}
