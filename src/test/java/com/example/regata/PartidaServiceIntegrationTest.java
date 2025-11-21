package com.example.regata;

import com.example.regata.entity.*;
import com.example.regata.repository.*;
import com.example.regata.service.PartidaService;
import com.example.regata.web.dto.partida.CreatePartidaRequest;
import com.example.regata.web.dto.partida.EstadoPartidaDto;
import com.example.regata.web.dto.partida.JoinPartidaRequest;
import com.example.regata.web.dto.partida.TurnoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // cada test se revierte
class PartidaServiceIntegrationTest {

    @Autowired
    private PartidaService partidaService;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private BarcoRepository barcoRepo;

    private Usuario player1;
    private Usuario player2;
    private Barco barco1;
    private Barco barco2;

    @BeforeEach
    void setUp() {
        // DbInitializer ya creó Player 1..5 y barcos
        List<Usuario> players = usuarioRepo.findAll();
        player1 = players.stream()
                .filter(u -> "Player 1".equals(u.getNombre()))
                .findFirst()
                .orElseThrow();

        player2 = players.stream()
                .filter(u -> "Player 2".equals(u.getNombre()))
                .findFirst()
                .orElseThrow();

        barco1 = barcoRepo.findByUsuarioId(player1.getId()).getFirst();
        barco2 = barcoRepo.findByUsuarioId(player2.getId()).getFirst();
    }

    @Test
    void flujo_completo_crear_unirse_iniciar_y_mover() {
        // 1) Crear partida (sin mapaId → usa el Mapa Fijo del DbInitializer)
        CreatePartidaRequest crearReq = new CreatePartidaRequest();
        crearReq.setNombre("Partida de prueba");
        crearReq.setMaxJugadores(4);

        EstadoPartidaDto estado = partidaService.crear(crearReq);
        Long partidaId = estado.getId();

        assertThat(estado.getEstado()).isEqualTo(PartidaEstado.WAITING);
        assertThat(estado.getLayout()).isNotEmpty();

        // 2) Unir Player1
        JoinPartidaRequest join1 = new JoinPartidaRequest();
        join1.setUsuarioId(player1.getId());
        join1.setBarcoId(barco1.getId());

        estado = partidaService.unirse(partidaId, join1);
        assertThat(estado.getParticipantes()).hasSize(1);

        // 3) Unir Player2
        JoinPartidaRequest join2 = new JoinPartidaRequest();
        join2.setUsuarioId(player2.getId());
        join2.setBarcoId(barco2.getId());

        estado = partidaService.unirse(partidaId, join2);
        assertThat(estado.getParticipantes()).hasSize(2);

        // 4) Iniciar partida
        estado = partidaService.iniciar(partidaId);
        assertThat(estado.getEstado()).isEqualTo(PartidaEstado.RUNNING);

        // Localizar participante de Player1
        var me = estado.getParticipantes().stream()
                .filter(p -> p.getUsuarioId().equals(player1.getId()))
                .findFirst()
                .orElseThrow();

        int inicialX = me.getPosX();
        int inicialY = me.getPosY();

        // 5) Turno: acelerar una casilla hacia la derecha (accX=1, accY=0)
        TurnoRequest turnoReq = new TurnoRequest();
        turnoReq.setParticipanteId(me.getId());
        turnoReq.setAccX(1);
        turnoReq.setAccY(0);

        estado = partidaService.turno(partidaId, turnoReq);

        var meDespues = estado.getParticipantes().stream()
                .filter(p -> p.getId().equals(me.getId()))
                .findFirst()
                .orElseThrow();

        // vel debe ser (1,0) y pos desplazada 1 en X
        assertThat(meDespues.getVelX()).isEqualTo(1);
        assertThat(meDespues.getVelY()).isEqualTo(0);
        assertThat(meDespues.getPosX()).isEqualTo(inicialX + 1);
        assertThat(meDespues.getPosY()).isEqualTo(inicialY);
        assertThat(meDespues.isVivo()).isTrue();
    }
}
