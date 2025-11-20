package com.example.regata.web.dto.modelo;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatchModeloBarcoRequest {

    @Size(max = 80)
    private String nombre;

    @Size(max = 20)
    private String color;

    @Size(max = 255)
    private String descripcion;

    @Min(1)
    private Integer velMax;

    @Min(0)
    private Integer acelMax;

    @Min(0) @Max(100)
    private Integer maniobrabilidad;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getVelMax() { return velMax; }
    public void setVelMax(Integer velMax) { this.velMax = velMax; }

    public Integer getAcelMax() { return acelMax; }
    public void setAcelMax(Integer acelMax) { this.acelMax = acelMax; }

    public Integer getManiobrabilidad() { return maniobrabilidad; }
    public void setManiobrabilidad(Integer maniobrabilidad) { this.maniobrabilidad = maniobrabilidad; }
}
