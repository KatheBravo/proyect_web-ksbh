package com.example.regata.service.impl;

import com.example.regata.entity.AuthSession;
import com.example.regata.entity.Usuario;
import com.example.regata.repository.AuthSessionRepository;
import com.example.regata.repository.UsuarioRepository;
import com.example.regata.security.JwtService;
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
import java.util.List;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AuthSessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           AuthSessionRepository sessionRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {          // 👈 inyectamos JwtService
        this.usuarioRepository = usuarioRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
        return AuthResponse.ofTokenAndUsuario(s.getToken(), u);
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
        return AuthResponse.ofTokenAndUsuario(s.getToken(), u);
    }

    private AuthSession nuevaSesion(Usuario u) {
        AuthSession s = new AuthSession();
        s.setUsuario(u);

        // 👇 Aquí generamos el JWT firmado con tu jwt-private.pem
        String jwt = jwtService.createAccessToken(
                u.getId(),                     // asumo que es Long
                u.getEmail(),
                List.of("USER"),               // aquí metes los roles que quieras
                Map.of("nombre", u.getNombre())
        );

        s.setToken(jwt);
        s.setCreadoEn(Instant.now());
        s.setExpiraEn(Instant.now().plus(24, ChronoUnit.HOURS)); // 24h, igual que access-token-ttl
        s.setActivo(true);
        return s;
    }
}
