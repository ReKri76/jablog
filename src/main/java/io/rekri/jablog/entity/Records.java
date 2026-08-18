package io.rekri.jablog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "records")
@Getter
@Setter
public class Records {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account", referencedColumnName = "username", nullable = false)
    private Accounts account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="\"user\"", referencedColumnName = "nickname", nullable = false)
    private Users user;
}
