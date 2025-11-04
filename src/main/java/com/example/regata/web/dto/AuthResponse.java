package com.example.regata.web.dto;

import com.example.regata.entity.Jugador;

public class AuthResponse {

    private String token; // token de sesión simple (no JWT)
    private Long jugadorId;
    private String nombre;
    private String email;

    public static AuthResponse ofTokenAndJugador(String token, Jugador j) {
        AuthResponse r = new AuthResponse();
        r.setToken(token);
        r.setJugadorId(j.getId());
        r.setNombre(j.getNombre());
        r.setEmail(j.getEmail());
        return r;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getJugadorId() { return jugadorId; }
    public void setJugadorId(Long jugadorId) { this.jugadorId = jugadorId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
