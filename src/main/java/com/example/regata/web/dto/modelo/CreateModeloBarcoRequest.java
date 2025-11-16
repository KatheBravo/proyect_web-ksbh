package com.example.regata.web.dto.modelo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public class CreateModeloBarcoRequest {

    @NotBlank
    @Size(max = 80)
    private String nombre;

    @Size(max = 255)
    private String descripcion;

    @Min(1)
    private Integer velMax; // opcional; si es null, se usa default del entity

    @Min(0)
    private Integer acelMax; // opcional; si es null, default del entity

    @Min(0) @Max(100)
    private Integer maniobrabilidad; // opcional; si es null, default del entity

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getVelMax() { return velMax; }
    public void setVelMax(Integer velMax) { this.velMax = velMax; }
    public Integer getAcelMax() { return acelMax; }
    public void setAcelMax(Integer acelMax) { this.acelMax = acelMax; }
    public Integer getManiobrabilidad() { return maniobrabilidad; }
    public void setManiobrabilidad(Integer maniobrabilidad) { this.maniobrabilidad = maniobrabilidad; }
}
