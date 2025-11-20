package com.example.regata.service.impl;

import com.example.regata.entity.ModeloBarco;
import com.example.regata.repository.ModeloBarcoRepository;
import com.example.regata.service.ModeloBarcoService;
import com.example.regata.web.dto.modelo.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ModeloBarcoServiceImpl implements ModeloBarcoService {

    private final ModeloBarcoRepository repo;

    public ModeloBarcoServiceImpl(ModeloBarcoRepository repo) {
        this.repo = repo;
    }

    @Override
    public ModeloBarcoDto create(CreateModeloBarcoRequest req) {
        // OJO: si cambiaste la unique constraint a (creado_por_id, nombre),
        // este exists puede necesitar luego el usuario creador.
        if (repo.existsByNombreIgnoreCase(req.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nombre de modelo ya existe");
        }

        ModeloBarco m = new ModeloBarco();
        m.setNombre(req.getNombre());
        m.setColor(req.getColor());
        m.setDescripcion(req.getDescripcion());

        // Solo sobreescribimos si vienen; si no, se quedan defaults del entity
        if (req.getVelMax() != null) {
            m.setVelMax(req.getVelMax());
        }
        if (req.getAcelMax() != null) {
            m.setAcelMax(req.getAcelMax());
        }
        if (req.getManiobrabilidad() != null) {
            m.setManiobrabilidad(req.getManiobrabilidad());
        }

        return toDto(repo.save(m));
    }

    @Override
    public ModeloBarcoDto get(Long id) {
        return toDto(find(id));
    }

    @Override
    public List<ModeloBarcoDto> list(String q) {
        List<ModeloBarco> all = repo.findAll();
        return all.stream()
                .filter(m -> q == null
                        || m.getNombre().toLowerCase().contains(q.toLowerCase()))
                .map(this::toDto)
                .toList();
    }

    @Override
    public ModeloBarcoDto updatePut(Long id, UpdateModeloBarcoRequest req) {
        ModeloBarco m = find(id);

        if (!m.getNombre().equalsIgnoreCase(req.getNombre())
                && repo.existsByNombreIgnoreCase(req.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nombre de modelo ya existe");
        }

        m.setNombre(req.getNombre());
        m.setColor(req.getColor());
        m.setDescripcion(req.getDescripcion());
        m.setVelMax(req.getVelMax());
        m.setAcelMax(req.getAcelMax());
        m.setManiobrabilidad(req.getManiobrabilidad());

        return toDto(repo.save(m));
    }

    @Override
    public ModeloBarcoDto updatePatch(Long id, PatchModeloBarcoRequest req) {
        ModeloBarco m = find(id);

        if (req.getNombre() != null
                && !m.getNombre().equalsIgnoreCase(req.getNombre())
                && repo.existsByNombreIgnoreCase(req.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nombre de modelo ya existe");
        }

        if (req.getNombre() != null) {
            m.setNombre(req.getNombre());
        }
        if (req.getColor() != null) {
            m.setColor(req.getColor());
        }
        if (req.getDescripcion() != null) {
            m.setDescripcion(req.getDescripcion());
        }
        if (req.getVelMax() != null) {
            m.setVelMax(req.getVelMax());
        }
        if (req.getAcelMax() != null) {
            m.setAcelMax(req.getAcelMax());
        }
        if (req.getManiobrabilidad() != null) {
            m.setManiobrabilidad(req.getManiobrabilidad());
        }

        return toDto(repo.save(m));
    }

    @Override
    public void delete(Long id) {
        ModeloBarco m = find(id);
        try {
            repo.delete(m);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar: está referenciado por barcos"
            );
        }
    }

    // helpers
    private ModeloBarco find(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ModeloBarco no encontrado"));
    }

    private ModeloBarcoDto toDto(ModeloBarco m) {
        ModeloBarcoDto d = new ModeloBarcoDto();
        d.setId(m.getId());
        d.setNombre(m.getNombre());
        d.setColor(m.getColor());
        d.setDescripcion(m.getDescripcion());
        d.setVelMax(m.getVelMax());
        d.setAcelMax(m.getAcelMax());
        d.setManiobrabilidad(m.getManiobrabilidad());
        d.setCreadoEn(m.getCreadoEn());
        d.setActualizadoEn(m.getActualizadoEn());
        return d;
    }
}
