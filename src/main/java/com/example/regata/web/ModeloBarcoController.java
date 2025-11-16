package com.example.regata.web;

import com.example.regata.service.ModeloBarcoService;
import com.example.regata.web.dto.modelo.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modelos")
public class ModeloBarcoController {

    private final ModeloBarcoService service;

    public ModeloBarcoController(ModeloBarcoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ModeloBarcoDto> create(@Valid @RequestBody CreateModeloBarcoRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModeloBarcoDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<List<ModeloBarcoDto>> list(@RequestParam(required = false, name = "q") String q) {
        return ResponseEntity.ok(service.list(q));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModeloBarcoDto> updatePut(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateModeloBarcoRequest req) {
        return ResponseEntity.ok(service.updatePut(id, req));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ModeloBarcoDto> updatePatch(@PathVariable Long id,
                                                      @RequestBody PatchModeloBarcoRequest req) {
        return ResponseEntity.ok(service.updatePatch(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
