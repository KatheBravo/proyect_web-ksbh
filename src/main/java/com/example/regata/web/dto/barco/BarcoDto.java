package com.example.regata.web.dto.barco;

public class BarcoDto {

    private Long id;
    private String nombre;
    private String color;

    private Long usuarioId;
    private String usuarioNombre;

    private Long modeloId;
    private String modeloNombre;

    // NUEVO
    private int posX;
    private int posY;
    private int velX;
    private int velY;

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

    public int getPosX() { return posX; }
    public void setPosX(int posX) { this.posX = posX; }

    public int getPosY() { return posY; }
    public void setPosY(int posY) { this.posY = posY; }

    public int getVelX() { return velX; }
    public void setVelX(int velX) { this.velX = velX; }

    public int getVelY() { return velY; }
    public void setVelY(int velY) { this.velY = velY; }
}
