package com.example.regata.service;

import com.example.regata.web.dto.usuario.*;

import java.util.List;

public interface UsuarioService {
    UsuarioDto create(CreateUsuarioRequest req);
    UsuarioDto get(Long id);
    List<UsuarioDto> list(String role, Boolean activo, String qEmail);
    UsuarioDto updatePut(Long id, UpdateUsuarioRequest req);
    UsuarioDto updatePatch(Long id, PatchUsuarioRequest req);
    void delete(Long id);
}
