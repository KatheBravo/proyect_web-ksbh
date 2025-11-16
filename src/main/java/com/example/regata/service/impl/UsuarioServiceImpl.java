package com.example.regata.service.impl;

import com.example.regata.entity.Role;
import com.example.regata.entity.Usuario;
import com.example.regata.repository.UsuarioRepository;
import com.example.regata.service.UsuarioService;
import com.example.regata.web.dto.usuario.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioServiceImpl(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public UsuarioDto create(CreateUsuarioRequest req) {
        if (repo.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado");
        }
        Usuario u = new Usuario();
        u.setNombre(req.getNombre());
        u.setEmail(req.getEmail());
        u.setPasswordHash(encoder.encode(req.getPassword()));
        if (req.getRole() != null && !req.getRole().isBlank()) {
            u.setRole(parseRole(req.getRole()));
        }
        if (req.getActivo() != null) u.setActivo(req.getActivo());
        return toDto(repo.save(u));
    }

    @Override
    public UsuarioDto get(Long id) {
        return toDto(find(id));
    }

    @Override
    public List<UsuarioDto> list(String role, Boolean activo, String qEmail) {
        List<Usuario> base = repo.findAll();
        return base.stream()
                .filter(u -> role == null || u.getRole() == parseRole(role))
                .filter(u -> activo == null || u.isActivo() == activo)
                .filter(u -> qEmail == null || u.getEmail().toLowerCase().contains(qEmail.toLowerCase()))
                .map(this::toDto)
                .toList();
    }

    @Override
    public UsuarioDto updatePut(Long id, UpdateUsuarioRequest req) {
        Usuario u = find(id);

        if (!u.getEmail().equalsIgnoreCase(req.getEmail()) && repo.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado");
        }

        u.setNombre(req.getNombre());
        u.setEmail(req.getEmail());
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            u.setPasswordHash(encoder.encode(req.getPassword()));
        }
        u.setRole(parseRole(req.getRole()));
        if (req.getActivo() != null) u.setActivo(req.getActivo());

        return toDto(repo.save(u));
    }

    @Override
    public UsuarioDto updatePatch(Long id, PatchUsuarioRequest req) {
        Usuario u = find(id);

        if (req.getEmail() != null && !u.getEmail().equalsIgnoreCase(req.getEmail())
                && repo.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado");
        }

        if (req.getNombre() != null) u.setNombre(req.getNombre());
        if (req.getEmail() != null) u.setEmail(req.getEmail());
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            u.setPasswordHash(encoder.encode(req.getPassword()));
        }
        if (req.getRole() != null) u.setRole(parseRole(req.getRole()));
        if (req.getActivo() != null) u.setActivo(req.getActivo());

        return toDto(repo.save(u));
    }

    @Override
    public void delete(Long id) {
        Usuario u = find(id);
        try {
            repo.delete(u);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar: tiene referencias");
        }
    }

    // ---- helpers ----
    private Usuario find(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private Role parseRole(String role) {
        try {
            return Role.valueOf(role.trim().toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Role inválido (usa ADMIN o PLAYER)");
        }
    }

    private UsuarioDto toDto(Usuario u) {
        UsuarioDto d = new UsuarioDto();
        d.setId(u.getId());
        d.setNombre(u.getNombre());
        d.setEmail(u.getEmail());
        d.setRole(u.getRole());
        d.setActivo(u.isActivo());
        d.setCreadoEn(u.getCreadoEn());
        d.setActualizadoEn(u.getActualizadoEn());
        return d;
    }
}
