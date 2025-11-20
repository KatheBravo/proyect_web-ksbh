package com.example.regata.web.dto.partida;

import jakarta.validation.constraints.Min;

public class CreatePartidaRequest {

    // Nombre opcional de la partida (si es null/blank, se usa el default en la entidad)
    private String nombre;

    // Si es null → usas el primer mapa disponible
    private Long mapaId;
    private Long hostUsuarioId;

    // Opcional; si viene, debe ser al menos 2 (si no viene, en el service usas default 4)
    @Min(2)
    private Integer maxJugadores;

    public Long getHostUsuarioId() {
        return hostUsuarioId;
    }

    public void setHostUsuarioId(Long hostUsuarioId) {
        this.hostUsuarioId = hostUsuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getMapaId() {
        return mapaId;
    }

    public void setMapaId(Long mapaId) {
        this.mapaId = mapaId;
    }

    public Integer getMaxJugadores() {
        return maxJugadores;
    }

    public void setMaxJugadores(Integer maxJugadores) {
        this.maxJugadores = maxJugadores;
    }
}
