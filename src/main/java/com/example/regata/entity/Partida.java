package com.example.regata.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "partidas")
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 120, nullable = false)
    private String nombre = "Partida";

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "mapa_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_partida_mapa"))
    private Mapa mapa;

    // Host puede ser null al crear
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "host_id", nullable = true,
            foreignKey = @ForeignKey(name = "fk_partida_host"))
    private Usuario host;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PartidaEstado estado = PartidaEstado.WAITING;

    @Column(name = "max_jugadores", nullable = false)
    private int maxJugadores = 4;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ganador_participante_id",
            foreignKey = @ForeignKey(name = "fk_partida_ganador"))
    private Participante ganador;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    @Column(name = "iniciado_en")
    private Instant iniciadoEn;

    @Column(name = "finalizado_en")
    private Instant finalizadoEn;

    @PrePersist
    void prePersist() {
        creadoEn = Instant.now();
    }

    // Getters / setters…

    public Long getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Mapa getMapa() { return mapa; }
    public void setMapa(Mapa mapa) { this.mapa = mapa; }

    public Usuario getHost() { return host; }
    public void setHost(Usuario host) { this.host = host; }

    public PartidaEstado getEstado() { return estado; }
    public void setEstado(PartidaEstado estado) { this.estado = estado; }

    public int getMaxJugadores() { return maxJugadores; }
    public void setMaxJugadores(int maxJugadores) { this.maxJugadores = maxJugadores; }

    public Participante getGanador() { return ganador; }
    public void setGanador(Participante ganador) { this.ganador = ganador; }

    public Instant getCreadoEn() { return creadoEn; }

    public Instant getIniciadoEn() { return iniciadoEn; }
    public void setIniciadoEn(Instant iniciadoEn) { this.iniciadoEn = iniciadoEn; }

    public Instant getFinalizadoEn() { return finalizadoEn; }
    public void setFinalizadoEn(Instant finalizadoEn) { this.finalizadoEn = finalizadoEn; }
}
