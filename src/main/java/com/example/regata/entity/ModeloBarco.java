package com.example.regata.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "modelos_barco", uniqueConstraints = {
        @UniqueConstraint(name = "uq_modelo_nombre", columnNames = {"nombre"})
})
public class ModeloBarco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    /** límite por componente de velocidad |dr|,|dc| (p.ej. 3) */
    @Column(nullable = false)
    private int velMax = 3;

    /** límite de aceleración por componente (normalmente 1) */
    @Column(nullable = false)
    private int acelMax = 1;

    /** de 0 a 100 (referencia futura para reglas) */
    @Column(nullable = false)
    private int maniobrabilidad = 100;

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
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getVelMax() { return velMax; }
    public void setVelMax(int velMax) { this.velMax = velMax; }
    public int getAcelMax() { return acelMax; }
    public void setAcelMax(int acelMax) { this.acelMax = acelMax; }
    public int getManiobrabilidad() { return maniobrabilidad; }
    public void setManiobrabilidad(int maniobrabilidad) { this.maniobrabilidad = maniobrabilidad; }
    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
    public Instant getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(Instant actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
