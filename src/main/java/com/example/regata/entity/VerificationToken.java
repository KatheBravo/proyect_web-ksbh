package com.example.regata.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity @Table(name="verification_tokens", indexes = @Index(columnList="token", unique = true))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VerificationToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=200)
    private String token;

    @ManyToOne(optional=false) @JoinColumn(name="user_id")
    private Usuario user;

    @Column(nullable=false)
    private Instant expiresAt;

    @Column(nullable=false)
    private boolean used = false;
}
