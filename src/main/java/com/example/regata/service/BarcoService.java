package com.example.regata.service;

import com.example.regata.web.dto.barco.*;

import java.util.List;

public interface BarcoService {
    BarcoDto create(CreateBarcoRequest req);
    BarcoDto get(Long id);
    List<BarcoDto> list(Long usuarioId); // null = todos
    BarcoDto updatePut(Long id, UpdateBarcoRequest req);
    BarcoDto updatePatch(Long id, PatchBarcoRequest req);
    void delete(Long id);
}
