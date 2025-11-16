package com.example.regata.web;

import com.example.regata.service.BarcoService;
import com.example.regata.web.dto.barco.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barcos")
public class BarcoController {

    private final BarcoService service;

    public BarcoController(BarcoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BarcoDto> create(@Valid @RequestBody CreateBarcoRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BarcoDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<List<BarcoDto>> list(
            @RequestParam(value = "usuarioId", required = false) Long usuarioId) {
        return ResponseEntity.ok(service.list(usuarioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BarcoDto> updatePut(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBarcoRequest req) {
        return ResponseEntity.ok(service.updatePut(id, req));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BarcoDto> updatePatch(
            @PathVariable Long id,
            @RequestBody PatchBarcoRequest req) {
        return ResponseEntity.ok(service.updatePatch(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
