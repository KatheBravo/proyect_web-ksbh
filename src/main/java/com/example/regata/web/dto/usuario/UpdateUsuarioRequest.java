package com.example.regata.web.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUsuarioRequest {

    @NotBlank @Size(max = 100)
    private String nombre;

    @NotBlank @Email @Size(max = 120)
    private String email;

    /** Si viene, se re-codifica; si no, se conserva */
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank
    private String role; // "ADMIN" o "PLAYER"

    private Boolean activo;

    // getters/setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
