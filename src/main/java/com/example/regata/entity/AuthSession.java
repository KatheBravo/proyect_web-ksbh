package com.example.regata.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "auth_sessions", indexes = {
        @Index(name = "idx_session_token", columnList = "token", unique = true)
})
public class  AuthSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_session_jugador"))
    private Jugador jugador;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    @Column(name = "expira_en", nullable = false)
    private Instant expiraEn;

    @Column(nullable = false)
    private boolean activo = true;

    @PrePersist
    void prePersist() {
        if (creadoEn == null) {
            creadoEn = Instant.now();
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Jugador getJugador() { return jugador; }
    public void setJugador(Jugador jugador) { this.jugador = jugador; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }

    public Instant getExpiraEn() { return expiraEn; }
    public void setExpiraEn(Instant expiraEn) { this.expiraEn = expiraEn; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
