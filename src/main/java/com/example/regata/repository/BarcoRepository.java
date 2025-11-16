package com.example.regata.repository;

import com.example.regata.entity.Barco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarcoRepository extends JpaRepository<Barco, Long> {
    List<Barco> findByUsuarioId(Long usuarioId); // <-- antes: findByJugadorId
}
