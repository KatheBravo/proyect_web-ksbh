package com.example.regata.web.dto.modelo;

import java.time.Instant;

public class ModeloBarcoDto {

    private Long id;
    private String nombre;
    private String color;
    private String descripcion;
    private int velMax;
    private int acelMax;
    private int maniobrabilidad;
    private Instant creadoEn;
    private Instant actualizadoEn;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

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
