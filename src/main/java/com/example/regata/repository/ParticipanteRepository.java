package com.example.regata.repository;

import com.example.regata.entity.Participante;
import com.example.regata.entity.Partida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipanteRepository extends JpaRepository<Participante, Long> {
    List<Participante> findByPartidaId(Long partidaId);
    int countByPartidaId(Long partidaId);
    boolean existsByPartidaIdAndUsuarioId(Long partidaId, Long usuarioId);
    boolean existsByPartidaIdAndBarcoId(Long partidaId, Long barcoId);
    Optional<Participante> findByIdAndPartidaId(Long id, Long partidaId);
}
