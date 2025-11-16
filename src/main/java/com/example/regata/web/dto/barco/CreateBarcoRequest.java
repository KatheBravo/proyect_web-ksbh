package com.example.regata.web.dto.barco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateBarcoRequest {

    @NotBlank @Size(max = 80)
    private String nombre;

    @Size(max = 20)
    private String color;

    @NotNull
    private Long usuarioId;   // antes: jugadorId

    @NotNull
    private Long modeloId;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getModeloId() { return modeloId; }
    public void setModeloId(Long modeloId) { this.modeloId = modeloId; }
}
