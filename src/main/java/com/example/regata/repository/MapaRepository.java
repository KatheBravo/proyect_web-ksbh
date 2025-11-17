package com.example.regata.repository;

import com.example.regata.entity.Mapa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MapaRepository extends JpaRepository<Mapa, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}
