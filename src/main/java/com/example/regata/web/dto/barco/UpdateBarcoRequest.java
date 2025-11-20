package com.example.regata.web.dto.barco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateBarcoRequest {

    @NotBlank
    @Size(max = 80)
    private String nombre;

    @NotNull
    private Long usuarioId;

    @NotNull
    private Long modeloId;

    // NUEVO (requeridos en PUT)
    @NotNull
    private Integer posX;

    @NotNull
    private Integer posY;

    @NotNull
    private Integer velX;

    @NotNull
    private Integer velY;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

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
