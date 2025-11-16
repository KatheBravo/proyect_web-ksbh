package com.example.regata.web.dto.barco;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatchBarcoRequest {

    @Size(max = 80)
    private String nombre;

    @Size(max = 20)
    private String color;

    private Long usuarioId;   // antes: jugadorId
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
