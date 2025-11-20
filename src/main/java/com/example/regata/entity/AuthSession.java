package com.example.regata.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "auth_sessions", indexes = {
        @Index(name = "idx_session_token", columnList = "token", unique = true)
})
public class AuthSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mantenemos la columna 'jugador_id' para no romper la BD,
    // pero el tipo ahora es Usuario
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_session_jugador"))
    private Usuario usuario;

    @Column(name = "token", length = 1024, nullable = false, unique = true)
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

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }

    public Instant getExpiraEn() { return expiraEn; }
    public void setExpiraEn(Instant expiraEn) { this.expiraEn = expiraEn; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
