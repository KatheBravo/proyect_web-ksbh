package com.example.regata.web.dto.partida;

import jakarta.validation.constraints.NotNull;

public class JoinPartidaRequest {
    @NotNull
    private Long usuarioId;
    @NotNull
    private Long barcoId;

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getBarcoId() { return barcoId; }
    public void setBarcoId(Long barcoId) { this.barcoId = barcoId; }
}
