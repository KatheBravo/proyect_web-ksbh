package com.example.regata.service;

import com.example.regata.web.dto.modelo.*;

import java.util.List;

public interface ModeloBarcoService {
    ModeloBarcoDto create(CreateModeloBarcoRequest req);
    ModeloBarcoDto get(Long id);
    List<ModeloBarcoDto> list(String q); // filtro por nombre contiene
    ModeloBarcoDto updatePut(Long id, UpdateModeloBarcoRequest req);
    ModeloBarcoDto updatePatch(Long id, PatchModeloBarcoRequest req);
    void delete(Long id);
}
