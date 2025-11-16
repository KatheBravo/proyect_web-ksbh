package com.example.regata.service.impl;

import com.example.regata.entity.AuthSession;
import com.example.regata.entity.Usuario;
import com.example.regata.repository.AuthSessionRepository;
import com.example.regata.repository.UsuarioRepository;
import com.example.regata.service.AuthService;
import com.example.regata.web.dto.AuthResponse;
import com.example.regata.web.dto.LoginRequest;
import com.example.regata.web.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AuthSessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           AuthSessionRepository sessionRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }
        Usuario u = new Usuario();
        u.setNombre(request.getNombre());
        u.setEmail(request.getEmail());
        u.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        u = usuarioRepository.save(u);

        AuthSession s = nuevaSesion(u);
        sessionRepository.save(s);
        return AuthResponse.ofTokenAndUsuario(s.getToken(), u); // <-- cambia a Usuario
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Usuario u = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!u.isActivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cuenta desactivada");
        }
        if (!passwordEncoder.matches(request.getPassword(), u.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        AuthSession s = nuevaSesion(u);
        sessionRepository.save(s);
        return AuthResponse.ofTokenAndUsuario(s.getToken(), u); // <-- cambia a Usuario
    }

    private AuthSession nuevaSesion(Usuario u) {
        AuthSession s = new AuthSession();
        s.setUsuario(u); // <-- cambia a Usuario
        s.setToken(UUID.randomUUID().toString().replace("-", ""));
        s.setCreadoEn(Instant.now());
        s.setExpiraEn(Instant.now().plus(24, ChronoUnit.HOURS));
        s.setActivo(true);
        return s;
    }
}
