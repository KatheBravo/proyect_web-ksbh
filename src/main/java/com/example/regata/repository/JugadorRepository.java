package com.example.regata.repository;

import com.example.regata.entity.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JugadorRepository extends JpaRepository<Jugador, Long> {
    boolean existsByEmail(String email);
    Optional<Jugador> findByEmail(String email);
}
