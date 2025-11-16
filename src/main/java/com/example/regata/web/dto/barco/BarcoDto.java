package com.example.regata.web.dto.barco;

public class BarcoDto {

    private Long id;
    private String nombre;
    private String color;

    private Long usuarioId;       // antes: jugadorId
    private String usuarioNombre; // antes: jugadorNombre

    private Long modeloId;
    private String modeloNombre;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public Long getModeloId() { return modeloId; }
    public void setModeloId(Long modeloId) { this.modeloId = modeloId; }

    public String getModeloNombre() { return modeloNombre; }
    public void setModeloNombre(String modeloNombre) { this.modeloNombre = modeloNombre; }
}
