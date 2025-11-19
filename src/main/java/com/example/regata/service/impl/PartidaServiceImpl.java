package com.example.regata.service.impl;

import com.example.regata.entity.*;
import com.example.regata.repository.*;
import com.example.regata.service.PartidaService;
import com.example.regata.web.dto.partida.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartidaServiceImpl implements PartidaService {

    private final PartidaRepository partidaRepo;
    private final ParticipanteRepository participanteRepo;
    private final MapaRepository mapaRepo;
    private final UsuarioRepository usuarioRepo;
    private final BarcoRepository barcoRepo;

    public PartidaServiceImpl(PartidaRepository partidaRepo,
                              ParticipanteRepository participanteRepo,
                              MapaRepository mapaRepo,
                              UsuarioRepository usuarioRepo,
                              BarcoRepository barcoRepo) {
        this.partidaRepo = partidaRepo;
        this.participanteRepo = participanteRepo;
        this.mapaRepo = mapaRepo;
        this.usuarioRepo = usuarioRepo;
        this.barcoRepo = barcoRepo;
    }

    // ─────────────────────────── crear ───────────────────────────
    @Override
    public EstadoPartidaDto crear(CreatePartidaRequest req) {
        Mapa mapa = (req.getMapaId() != null)
                ? mapaRepo.findById(req.getMapaId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "mapaId no existe"))
                : mapaRepo.findAll().stream().findFirst().orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED, "No hay mapas creados"));

        Partida p = new Partida();
        p.setMapa(mapa);
        if (req.getNombre() != null && !req.getNombre().isBlank()) p.setNombre(req.getNombre());
        if (req.getMaxJugadores() != null) p.setMaxJugadores(req.getMaxJugadores());
        p = partidaRepo.save(p);

        return toEstadoDto(p, participantesDe(p.getId()), mapaLines(mapa));
    }

    // ─────────────────────────── unirse ───────────────────────────
    @Override
    public EstadoPartidaDto unirse(Long partidaId, JoinPartidaRequest req) {
        Partida p = findPartida(partidaId);
        if (p.getEstado() != PartidaEstado.WAITING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La partida no está en WAITING");
        }

        if (participanteRepo.countByPartidaId(partidaId) >= p.getMaxJugadores()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Partida llena");
        }

        Usuario u = usuarioRepo.findById(req.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "usuarioId no existe"));
        Barco b = barcoRepo.findById(req.getBarcoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "barcoId no existe"));

        if (!Objects.equals(b.getUsuario().getId(), u.getId())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "El barco no pertenece al usuario");
        }
        if (participanteRepo.existsByPartidaIdAndUsuarioId(partidaId, u.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya está en la partida");
        }
        if (participanteRepo.existsByPartidaIdAndBarcoId(partidaId, b.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese barco ya está en la partida");
        }

        // Asignar posición inicial en una 'P'
        List<String> lines = mapaLines(p.getMapa());
        List<int[]> starts = findStarts(lines); // [x,y]
        if (starts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "El mapa no tiene posiciones 'P'");
        }
        int orden = participanteRepo.countByPartidaId(partidaId);
        int[] s = starts.get(orden % starts.size());

        Participante part = new Participante();
        part.setPartida(p);
        part.setUsuario(u);
        part.setBarco(b);
        part.setPosX(s[0]);
        part.setPosY(s[1]);
        part.setVelX(0);
        part.setVelY(0);
        part.setVivo(true);
        part.setLlegoMeta(false);
        part.setOrden(orden);
        participanteRepo.save(part);

        return toEstadoDto(p, participantesDe(partidaId), lines);
    }

    // ─────────────────────────── iniciar ───────────────────────────
    @Override
    public EstadoPartidaDto iniciar(Long partidaId) {
        Partida p = findPartida(partidaId);
        if (p.getEstado() != PartidaEstado.WAITING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La partida no está en WAITING");
        }
        if (participanteRepo.countByPartidaId(partidaId) < 2) {
            // Si quieres permitir 1vCPU, cambia este número a 1
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Se requieren al menos 2 participantes");
        }
        p.setEstado(PartidaEstado.RUNNING);
        p.setIniciadoEn(Instant.now());
        partidaRepo.save(p);

        return toEstadoDto(p, participantesDe(partidaId), mapaLines(p.getMapa()));
    }

    // ─────────────────────────── estado ───────────────────────────
    @Override
    public EstadoPartidaDto estado(Long partidaId) {
        Partida p = findPartida(partidaId);
        return toEstadoDto(p, participantesDe(partidaId), mapaLines(p.getMapa()));
    }

    // ─────────────────────────── turno ───────────────────────────
    @Override
    public EstadoPartidaDto turno(Long partidaId, TurnoRequest req) {
        Partida p = findPartida(partidaId);
        if (p.getEstado() != PartidaEstado.RUNNING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La partida no está en RUNNING");
        }
        Participante part = participanteRepo.findByIdAndPartidaId(req.getParticipanteId(), partidaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "participanteId inválido"));

        if (!part.isVivo() || part.isLlegoMeta()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El participante no puede moverse");
        }

        Barco barco = part.getBarco();
        ModeloBarco modelo = barco.getModelo();

        int accX = req.getAccX();
        int accY = req.getAccY();

        // Validar aceleración |acc| <= acelMax
        if (Math.abs(accX) > modelo.getAcelMax() || Math.abs(accY) > modelo.getAcelMax()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Aceleración inválida (excede acelMax)");
        }

        // actualizar velocidad con clamp por componente
        int newVelX = clamp(part.getVelX() + accX, -modelo.getVelMax(), modelo.getVelMax());
        int newVelY = clamp(part.getVelY() + accY, -modelo.getVelMax(), modelo.getVelMax());

        int newX = part.getPosX() + newVelX;
        int newY = part.getPosY() + newVelY;

        List<String> lines = mapaLines(p.getMapa());

        // Fuera de límites → destruido
        if (!inBounds(lines, newX, newY)) {
            part.setVivo(false);
            participanteRepo.save(part);
            return toEstadoDto(p, participantesDe(partidaId), lines);
        }

        char cell = lines.get(newY).charAt(newX);
        if (cell == 'X') {
            // choca pared → destruido (se queda en su posición actual)
            part.setVivo(false);
            participanteRepo.save(part);
            return toEstadoDto(p, participantesDe(partidaId), lines);
        }

        // Mover
        part.setVelX(newVelX);
        part.setVelY(newVelY);
        part.setPosX(newX);
        part.setPosY(newY);

        // Meta
        if (cell == 'M' || cell == 'm') {
            part.setLlegoMeta(true);
            participanteRepo.save(part);

            p.setEstado(PartidaEstado.FINISHED);
            p.setGanador(part);
            p.setFinalizadoEn(Instant.now());
            partidaRepo.save(p);

            return toEstadoDto(p, participantesDe(partidaId), lines);
        }

        participanteRepo.save(part);
        return toEstadoDto(p, participantesDe(partidaId), lines);
    }

    // ─────────────────────────── helpers ───────────────────────────
    private Partida findPartida(Long id) {
        return partidaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida no encontrada"));
    }

    private List<Participante> participantesDe(Long partidaId) {
        return participanteRepo.findByPartidaId(partidaId);
    }

    private List<String> mapaLines(Mapa mapa) {
        return Arrays.stream(mapa.getLayout().split("\n")).collect(Collectors.toList());
    }

    private List<int[]> findStarts(List<String> lines) {
        List<int[]> starts = new ArrayList<>();
        for (int y = 0; y < lines.size(); y++) {
            String row = lines.get(y);
            for (int x = 0; x < row.length(); x++) {
                if (row.charAt(x) == 'P') starts.add(new int[]{x, y});
            }
        }
        return starts;
    }

    private boolean inBounds(List<String> lines, int x, int y) {
        return y >= 0 && y < lines.size() && x >= 0 && x < lines.get(y).length();
    }

    private int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    // mapping
    private EstadoPartidaDto toEstadoDto(Partida p, List<Participante> parts, List<String> lines) {
        EstadoPartidaDto dto = new EstadoPartidaDto();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setEstado(p.getEstado());
        dto.setMapaNombre(p.getMapa().getNombre());
        dto.setLayout(lines);
        dto.setMaxJugadores(p.getMaxJugadores());
        dto.setGanadorParticipanteId(p.getGanador() != null ? p.getGanador().getId() : null);

        List<ParticipanteDto> pdtos = parts.stream().map(part -> {
            ParticipanteDto d = new ParticipanteDto();
            d.setId(part.getId());
            d.setUsuarioId(part.getUsuario().getId());
            d.setUsuarioNombre(part.getUsuario().getNombre());
            d.setBarcoId(part.getBarco().getId());
            d.setBarcoNombre(part.getBarco().getNombre());
            d.setPosX(part.getPosX());
            d.setPosY(part.getPosY());
            d.setVelX(part.getVelX());
            d.setVelY(part.getVelY());
            d.setVivo(part.isVivo());
            d.setLlegoMeta(part.isLlegoMeta());
            d.setOrden(part.getOrden());
            return d;
        }).toList();
        dto.setParticipantes(pdtos);
        return dto;
    }
}
