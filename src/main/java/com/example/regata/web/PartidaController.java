package com.example.regata.web;

import com.example.regata.service.PartidaService;
import com.example.regata.web.dto.partida.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

    private final PartidaService service;

    public PartidaController(PartidaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EstadoPartidaDto> crear(@Valid @RequestBody CreatePartidaRequest req) {
        return ResponseEntity.ok(service.crear(req));
    }

    @GetMapping
    public ResponseEntity<List<EstadoPartidaDto>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<EstadoPartidaDto> join(@PathVariable Long id,
                                                 @Valid @RequestBody JoinPartidaRequest req) {
        return ResponseEntity.ok(service.unirse(id, req));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<EstadoPartidaDto> start(@PathVariable Long id) {
        return ResponseEntity.ok(service.iniciar(id));
    }

    @GetMapping("/{id}/estado")
    public ResponseEntity<EstadoPartidaDto> estado(@PathVariable Long id) {
        return ResponseEntity.ok(service.estado(id));
    }

    @PostMapping("/{id}/turno")
    public ResponseEntity<EstadoPartidaDto> turno(@PathVariable Long id,
                                                  @Valid @RequestBody TurnoRequest req) {
        return ResponseEntity.ok(service.turno(id, req));
    }
}
