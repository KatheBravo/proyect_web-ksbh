package com.example.regata.web.dto.modelo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateModeloBarcoRequest {

    @NotBlank
    @Size(max = 80)
    private String nombre;

    @Size(max = 255)
    private String descripcion;

    @Min(1)
    private int velMax;

    @Min(0)
    private int acelMax;

    @Min(0) @Max(100)
    private int maniobrabilidad;

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
}
