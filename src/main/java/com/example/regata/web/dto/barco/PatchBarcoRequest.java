package com.example.regata.web.dto.barco;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatchBarcoRequest {

    @Size(max = 80)
    private String nombre;

    @Size(max = 20)
    private String color;

    private Long usuarioId;
    private Long modeloId;

    // NUEVO (opcionales)
    private Integer posX;
    private Integer posY;
    private Integer velX;
    private Integer velY;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getModeloId() { return modeloId; }
    public void setModeloId(Long modeloId) { this.modeloId = modeloId; }

    public Integer getPosX() { return posX; }
    public void setPosX(Integer posX) { this.posX = posX; }
    public Integer getPosY() { return posY; }
    public void setPosY(Integer posY) { this.posY = posY; }
    public Integer getVelX() { return velX; }
    public void setVelX(Integer velX) { this.velX = velX; }
    public Integer getVelY() { return velY; }
    public void setVelY(Integer velY) { this.velY = velY; }
}
