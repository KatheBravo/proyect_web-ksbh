package com.example.regata.service.impl;

import com.example.regata.entity.Barco;
import com.example.regata.entity.Usuario;
import com.example.regata.entity.ModeloBarco;
import com.example.regata.repository.BarcoRepository;
import com.example.regata.repository.UsuarioRepository;
import com.example.regata.repository.ModeloBarcoRepository;
import com.example.regata.service.BarcoService;
import com.example.regata.web.dto.barco.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BarcoServiceImpl implements BarcoService {

    private final BarcoRepository barcoRepo;
    private final UsuarioRepository usuarioRepo;
    private final ModeloBarcoRepository modeloRepo;

    public BarcoServiceImpl(BarcoRepository barcoRepo,
                            UsuarioRepository usuarioRepo,
                            ModeloBarcoRepository modeloRepo) {
        this.barcoRepo = barcoRepo;
        this.usuarioRepo = usuarioRepo;
        this.modeloRepo = modeloRepo;
    }

    @Override
    public BarcoDto create(CreateBarcoRequest req) {
        Usuario owner = usuarioRepo.findById(req.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, "usuarioId no existe"));

        ModeloBarco modelo = modeloRepo.findById(req.getModeloId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, "modeloId no existe"));

        // Regla: solo modelos públicos o creados por el mismo usuario (si aplicas la idea)
        if (!modelo.isPublico()
                && (modelo.getCreadoPor() == null
                || !modelo.getCreadoPor().getId().equals(owner.getId()))) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "El usuario no puede usar este modelo");
        }

        Barco b = new Barco();
        b.setNombre(req.getNombre());
        b.setUsuario(owner);
        b.setModelo(modelo);

        // si no vienen, quedan en 0
        b.setPosX(req.getPosX() != null ? req.getPosX() : 0);
        b.setPosY(req.getPosY() != null ? req.getPosY() : 0);
        b.setVelX(req.getVelX() != null ? req.getVelX() : 0);
        b.setVelY(req.getVelY() != null ? req.getVelY() : 0);

        b = barcoRepo.save(b);
        return toDto(b);
    }

    @Override
    public BarcoDto get(Long id) {
        Barco b = barcoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Barco no encontrado"));
        return toDto(b);
    }

    @Override
    public List<BarcoDto> list(Long usuarioId) {
        List<Barco> barcos = (usuarioId == null)
                ? barcoRepo.findAll()
                : barcoRepo.findByUsuarioId(usuarioId);
        return barcos.stream().map(this::toDto).toList();
    }

    @Override
    public BarcoDto updatePut(Long id, UpdateBarcoRequest req) {
        Barco b = barcoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Barco no encontrado"));

        Usuario newOwner = usuarioRepo.findById(req.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, "usuarioId no existe"));

        ModeloBarco newModelo = modeloRepo.findById(req.getModeloId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, "modeloId no existe"));

        // Regla: solo modelos públicos o propios del owner nuevo
        if (!newModelo.isPublico()
                && (newModelo.getCreadoPor() == null
                || !newModelo.getCreadoPor().getId().equals(newOwner.getId()))) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "El usuario no puede usar este modelo");
        }

        b.setNombre(req.getNombre());
        b.setUsuario(newOwner);
        b.setModelo(newModelo);

        // (PUT: requeridos)
        b.setPosX(req.getPosX());
        b.setPosY(req.getPosY());
        b.setVelX(req.getVelX());
        b.setVelY(req.getVelY());

        b = barcoRepo.save(b);
        return toDto(b);
    }

    @Override
    public BarcoDto updatePatch(Long id, PatchBarcoRequest req) {
        Barco b = barcoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Barco no encontrado"));

        // Primero, si cambia el owner, actualizamos el usuario
        if (req.getUsuarioId() != null) {
            Usuario newOwner = usuarioRepo.findById(req.getUsuarioId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.UNPROCESSABLE_ENTITY, "usuarioId no existe"));
            b.setUsuario(newOwner);
        }

        if (req.getNombre() != null) {
            b.setNombre(req.getNombre());
        }

        // Si cambia el modelo, validar contra el usuario actual (ya actualizado si venía usuarioId)
        if (req.getModeloId() != null) {
            ModeloBarco newModelo = modeloRepo.findById(req.getModeloId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.UNPROCESSABLE_ENTITY, "modeloId no existe"));

            Usuario ownerActual = b.getUsuario();
            if (!newModelo.isPublico()
                    && (newModelo.getCreadoPor() == null
                    || !newModelo.getCreadoPor().getId().equals(ownerActual.getId()))) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "El usuario no puede usar este modelo");
            }

            b.setModelo(newModelo);
        }

        // NUEVO (PATCH: opcionales)
        if (req.getPosX() != null) b.setPosX(req.getPosX());
        if (req.getPosY() != null) b.setPosY(req.getPosY());
        if (req.getVelX() != null) b.setVelX(req.getVelX());
        if (req.getVelY() != null) b.setVelY(req.getVelY());

        b = barcoRepo.save(b);
        return toDto(b);
    }

    @Override
    public void delete(Long id) {
        Barco b = barcoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Barco no encontrado"));
        barcoRepo.delete(b);
    }

    private BarcoDto toDto(Barco b) {
        BarcoDto d = new BarcoDto();
        d.setId(b.getId());
        d.setNombre(b.getNombre());

        if (b.getUsuario() != null) {
            d.setUsuarioId(b.getUsuario().getId());
            d.setUsuarioNombre(b.getUsuario().getNombre());
        }

        if (b.getModelo() != null) {
            d.setModeloId(b.getModelo().getId());
            d.setModeloNombre(b.getModelo().getNombre());
        }

        d.setPosX(b.getPosX());
        d.setPosY(b.getPosY());
        d.setVelX(b.getVelX());
        d.setVelY(b.getVelY());

        return d;
    }
}
