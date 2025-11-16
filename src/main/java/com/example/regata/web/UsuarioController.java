package com.example.regata.web;

import com.example.regata.service.UsuarioService;
import com.example.regata.web.dto.usuario.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UsuarioDto> create(@Valid @RequestBody CreateUsuarioRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> list(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false, name = "q") String qEmail
    ) {
        return ResponseEntity.ok(service.list(role, activo, qEmail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> updatePut(@PathVariable Long id,
                                                @Valid @RequestBody UpdateUsuarioRequest req) {
        return ResponseEntity.ok(service.updatePut(id, req));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioDto> updatePatch(@PathVariable Long id,
                                                  @RequestBody PatchUsuarioRequest req) {
        return ResponseEntity.ok(service.updatePatch(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
