package io.rekri.jablog.entity;

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

    @Column(name="nickname",  nullable=false, unique=true, length = 10, updatable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    /**
     * isOwner
     * */
    @Column(name =  "role", nullable = false, updatable = false)
    private boolean role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board", referencedColumnName = "name")
    private Board board;
}
