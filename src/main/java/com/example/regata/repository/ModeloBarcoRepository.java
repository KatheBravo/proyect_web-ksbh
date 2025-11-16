package com.example.regata.repository;

import com.example.regata.entity.ModeloBarco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModeloBarcoRepository extends JpaRepository<ModeloBarco, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    Optional<ModeloBarco> findByNombreIgnoreCase(String nombre);
}
