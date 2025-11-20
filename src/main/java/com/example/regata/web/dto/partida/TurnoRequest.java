package com.example.regata.web.dto.partida;

import jakarta.validation.constraints.NotNull;

public class TurnoRequest {

    @NotNull
    private Long participanteId;

    // Aceleraciones propuestas; se validan contra acelMax del modelo de barco
    @NotNull
    private Integer accX;

    @NotNull
    private Integer accY;

    public Long getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(Long participanteId) {
        this.participanteId = participanteId;
    }

    public Integer getAccX() {
        return accX;
    }

    public void setAccX(Integer accX) {
        this.accX = accX;
    }

    public Integer getAccY() {
        return accY;
    }

    public void setAccY(Integer accY) {
        this.accY = accY;
    }
}
