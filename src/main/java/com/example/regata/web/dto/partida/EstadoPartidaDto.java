package com.example.regata.web.dto.partida;

import com.example.regata.entity.PartidaEstado;

import java.util.List;

public class EstadoPartidaDto {

    private Long id;
    private String nombre;
    private PartidaEstado estado;

    private String mapaNombre;
    private List<String> layout; // líneas ASCII del mapa

    private int maxJugadores;

    // Ganador (participante) si la partida terminó; null si no
    private Long ganadorParticipanteId;

    // 👇 Nuevo: info del host (dueño de la sala)
    private Long hostUsuarioId;
    private String hostUsuarioNombre;

    private List<ParticipanteDto> participantes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public PartidaEstado getEstado() {
        return estado;
    }

    public void setEstado(PartidaEstado estado) {
        this.estado = estado;
    }

    public String getMapaNombre() {
        return mapaNombre;
    }

    public void setMapaNombre(String mapaNombre) {
        this.mapaNombre = mapaNombre;
    }

    public List<String> getLayout() {
        return layout;
    }

    public void setLayout(List<String> layout) {
        this.layout = layout;
    }

    public int getMaxJugadores() {
        return maxJugadores;
    }

    public void setMaxJugadores(int maxJugadores) {
        this.maxJugadores = maxJugadores;
    }

    public Long getGanadorParticipanteId() {
        return ganadorParticipanteId;
    }

    public void setGanadorParticipanteId(Long ganadorParticipanteId) {
        this.ganadorParticipanteId = ganadorParticipanteId;
    }

    public Long getHostUsuarioId() {
        return hostUsuarioId;
    }

    public void setHostUsuarioId(Long hostUsuarioId) {
        this.hostUsuarioId = hostUsuarioId;
    }

    public String getHostUsuarioNombre() {
        return hostUsuarioNombre;
    }

    public void setHostUsuarioNombre(String hostUsuarioNombre) {
        this.hostUsuarioNombre = hostUsuarioNombre;
    }

    public List<ParticipanteDto> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<ParticipanteDto> participantes) {
        this.participantes = participantes;
    }
}
