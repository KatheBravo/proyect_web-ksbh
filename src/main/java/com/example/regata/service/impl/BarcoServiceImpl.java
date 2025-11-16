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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "usuarioId no existe"));

        ModeloBarco modelo = modeloRepo.findById(req.getModeloId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "modeloId no existe"));

        Barco b = new Barco();
        b.setNombre(req.getNombre());
        b.setColor(req.getColor());
        b.setUsuario(owner);
        b.setModelo(modelo);

        b = barcoRepo.save(b);
        return toDto(b);
    }

    @Override
    public BarcoDto get(Long id) {
        Barco b = barcoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Barco no encontrado"));
        return toDto(b);
    }

    @Override
    public List<BarcoDto> list(Long usuarioId) {
        List<Barco> barcos = (usuarioId == null)
                ? barcoRepo.findAll()
                : barcoRepo.findByUsuarioId(usuarioId); // <-- usar repo, no filtrar en memoria
        return barcos.stream().map(this::toDto).toList();
    }

    @Override
    public BarcoDto updatePut(Long id, UpdateBarcoRequest req) {
        Barco b = barcoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Barco no encontrado"));

        Usuario newOwner = usuarioRepo.findById(req.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "usuarioId no existe"));
        ModeloBarco newModelo = modeloRepo.findById(req.getModeloId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "modeloId no existe"));

        b.setNombre(req.getNombre());
        b.setColor(req.getColor());
        b.setUsuario(newOwner);
        b.setModelo(newModelo);

        b = barcoRepo.save(b);
        return toDto(b);
    }

    @Override
    public BarcoDto updatePatch(Long id, PatchBarcoRequest req) {
        Barco b = barcoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Barco no encontrado"));

        if (req.getNombre() != null) b.setNombre(req.getNombre());
        if (req.getColor() != null) b.setColor(req.getColor());
        if (req.getUsuarioId() != null) {
            Usuario newOwner = usuarioRepo.findById(req.getUsuarioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "usuarioId no existe"));
            b.setUsuario(newOwner);
        }
        if (req.getModeloId() != null) {
            ModeloBarco newModelo = modeloRepo.findById(req.getModeloId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "modeloId no existe"));
            b.setModelo(newModelo);
        }

        b = barcoRepo.save(b);
        return toDto(b);
    }

    @Override
    public void delete(Long id) {
        Barco b = barcoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Barco no encontrado"));
        barcoRepo.delete(b);
    }

    private BarcoDto toDto(Barco b) {
        BarcoDto d = new BarcoDto();
        d.setId(b.getId());
        d.setNombre(b.getNombre());
        d.setColor(b.getColor());
        if (b.getUsuario() != null) {
            d.setUsuarioId(b.getUsuario().getId());
            d.setUsuarioNombre(b.getUsuario().getNombre());
        }
        if (b.getModelo() != null) {
            d.setModeloId(b.getModelo().getId());
            d.setModeloNombre(b.getModelo().getNombre());
        }
        return d;
    }
}
