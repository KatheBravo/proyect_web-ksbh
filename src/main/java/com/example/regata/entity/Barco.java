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

    @Column(length = 20)
    private String color;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false, // mantenemos el nombre de columna
            foreignKey = @ForeignKey(name = "fk_barco_jugador"))
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "modelo_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_barco_modelo"))
    private ModeloBarco modelo;

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

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public ModeloBarco getModelo() { return modelo; }
    public void setModelo(ModeloBarco modelo) { this.modelo = modelo; }

    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }

    public Instant getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(Instant actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
