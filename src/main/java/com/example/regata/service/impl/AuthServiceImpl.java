package com.example.regata.service.impl;

import com.example.regata.entity.AuthSession;
import com.example.regata.entity.Jugador;
import com.example.regata.repository.AuthSessionRepository;
import com.example.regata.repository.JugadorRepository;
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
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final JugadorRepository jugadorRepository;
    private final AuthSessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(JugadorRepository jugadorRepository,
                           AuthSessionRepository sessionRepository,
                           PasswordEncoder passwordEncoder) {
        this.jugadorRepository = jugadorRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (jugadorRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }
        Jugador j = new Jugador();
        j.setNombre(request.getNombre());
        j.setEmail(request.getEmail());
        j.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        j = jugadorRepository.save(j);

        AuthSession s = nuevaSesion(j);
        sessionRepository.save(s);
        return AuthResponse.ofTokenAndJugador(s.getToken(), j);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Optional<Jugador> opt = jugadorRepository.findByEmail(request.getEmail());
        if (opt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }
        Jugador j = opt.get();
        if (!j.isActivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cuenta desactivada");
        }
        if (!passwordEncoder.matches(request.getPassword(), j.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }
        AuthSession s = nuevaSesion(j);
        sessionRepository.save(s);
        return AuthResponse.ofTokenAndJugador(s.getToken(), j);
    }

    private AuthSession nuevaSesion(Jugador j) {
        AuthSession s = new AuthSession();
        s.setJugador(j);
        s.setToken(UUID.randomUUID().toString().replace("-", ""));
        s.setCreadoEn(Instant.now());
        s.setExpiraEn(Instant.now().plus(24, ChronoUnit.HOURS));
        s.setActivo(true);
        return s;
    }
}
