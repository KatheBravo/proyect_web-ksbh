package com.example.regata.service;

import com.example.regata.web.dto.partida.*;

public interface PartidaService {
    EstadoPartidaDto crear(CreatePartidaRequest req);
    EstadoPartidaDto unirse(Long partidaId, JoinPartidaRequest req);
    EstadoPartidaDto iniciar(Long partidaId);
    EstadoPartidaDto estado(Long partidaId);
    EstadoPartidaDto turno(Long partidaId, TurnoRequest req);
}
