package com.example.regata.web.dto.partida;

import jakarta.validation.constraints.Min;

public class CreatePartidaRequest {
    private String nombre;     // opcional
    private Long mapaId;       // opcional: si null, usa el primero
    @Min(2)
    private Integer maxJugadores; // opcional (default 4)

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Long getMapaId() { return mapaId; }
    public void setMapaId(Long mapaId) { this.mapaId = mapaId; }
    public Integer getMaxJugadores() { return maxJugadores; }
    public void setMaxJugadores(Integer maxJugadores) { this.maxJugadores = maxJugadores; }
}
