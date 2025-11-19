package com.example.regata.web.dto.partida;

import jakarta.validation.constraints.NotNull;

public class TurnoRequest {
    @NotNull
    private Long participanteId;
    @NotNull
    private Integer accX; // -1,0,1 (o +-acelMax)
    @NotNull
    private Integer accY;

    public Long getParticipanteId() { return participanteId; }
    public void setParticipanteId(Long participanteId) { this.participanteId = participanteId; }
    public Integer getAccX() { return accX; }
    public void setAccX(Integer accX) { this.accX = accX; }
    public Integer getAccY() { return accY; }
    public void setAccY(Integer accY) { this.accY = accY; }
}
