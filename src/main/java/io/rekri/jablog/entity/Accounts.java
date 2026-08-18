package io.rekri.jablog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Accounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NaturalId
    @Column(name = "username", nullable = false, updatable = false, length = 32, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "refresh_expired_time", nullable = false)
    private long refreshExpiredTime = Instant.now().toEpochMilli();

    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Records> records = new HashSet<Records>();
}
