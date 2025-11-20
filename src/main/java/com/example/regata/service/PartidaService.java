package com.example.regata.service;

import com.example.regata.web.dto.partida.EstadoPartidaDto;
import com.example.regata.web.dto.partida.CreatePartidaRequest;
import com.example.regata.web.dto.partida.JoinPartidaRequest;
import com.example.regata.web.dto.partida.TurnoRequest;

import java.util.List;

public interface PartidaService {
    EstadoPartidaDto crear(CreatePartidaRequest req);

    EstadoPartidaDto unirse(Long partidaId, JoinPartidaRequest req);

    EstadoPartidaDto iniciar(Long partidaId);

    EstadoPartidaDto estado(Long partidaId);

    EstadoPartidaDto turno(Long partidaId, TurnoRequest req);

    List<EstadoPartidaDto> listar();
}
