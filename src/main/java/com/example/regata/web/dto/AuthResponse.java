package com.example.regata.web.dto;

import com.example.regata.entity.Usuario;

public class AuthResponse {

    private String token;
    private Long usuarioId;
    private String nombre;
    private String email;

    public static AuthResponse ofTokenAndUsuario(String token, Usuario u) {
        AuthResponse r = new AuthResponse();
        r.setToken(token);
        r.setUsuarioId(u.getId());
        r.setNombre(u.getNombre());
        r.setEmail(u.getEmail());
        return r;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
