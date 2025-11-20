package com.example.regata.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "barcos")
public class Barco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_barco_jugador"))
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "modelo_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_barco_modelo"))
    private ModeloBarco modelo;

    // --- posición (fila/col) y velocidad (dr/dc) ---
    // Convención: posY = fila (r), posX = columna (c) del mapa ASCII.
    @Column(name = "pos_x", nullable = false)
    private int posX = 0;

    @Column(name = "pos_y", nullable = false)
    private int posY = 0;

    @Column(name = "vel_x", nullable = false)
    private int velX = 0;

    @Column(name = "vel_y", nullable = false)
    private int velY = 0;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    @PrePersist
    void prePersist() {
        creadoEn = Instant.now();
        actualizadoEn = creadoEn;
    }

    @PreUpdate
    void preUpdate() {
        actualizadoEn = Instant.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public ModeloBarco getModelo() { return modelo; }
    public void setModelo(ModeloBarco modelo) { this.modelo = modelo; }

    public int getPosX() { return posX; }
    public void setPosX(int posX) { this.posX = posX; }

    public int getPosY() { return posY; }
    public void setPosY(int posY) { this.posY = posY; }

    public int getVelX() { return velX; }
    public void setVelX(int velX) { this.velX = velX; }

    public int getVelY() { return velY; }
    public void setVelY(int velY) { this.velY = velY; }

    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }

    public Instant getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(Instant actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
