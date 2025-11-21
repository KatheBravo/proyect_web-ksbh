package com.example.regata;

import com.example.regata.entity.ModeloBarco;
import com.example.regata.entity.Usuario;
import com.example.regata.repository.ModeloBarcoRepository;
import com.example.regata.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Prueba de sistema: flujo largo usando varios endpoints:
 * crear partida -> crear barco -> join -> start -> turno.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class RegataSystemTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UsuarioRepository usuarioRepo;

    @Autowired
    ModeloBarcoRepository modeloRepo;

    @Test
    void flujoCompleto_crearPartida_unirJugador_iniciar_y_mover() throws Exception {
        // 1) Tomamos un jugador existente (Player 1) y un modelo cualquiera
        Usuario player = usuarioRepo.findByEmail("player1@regata.com").orElseThrow();
        ModeloBarco modelo = modeloRepo.findAll().get(0);

        // 2) Crear barco para ese jugador (POST /api/barcos)
        String barcoBody = """
            {
              "nombre": "BarcoSistema",
              "usuarioId": %d,
              "modeloId": %d
            }
            """.formatted(player.getId(), modelo.getId());

        MvcResult barcoRes = mockMvc.perform(post("/api/barcos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(barcoBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        JsonNode barcoJson = objectMapper.readTree(barcoRes.getResponse().getContentAsString());
        long barcoId = barcoJson.get("id").asLong();

        // 3) Crear partida (POST /api/partidas)
        String partidaBody = """
            {
              "nombre": "Partida Sistema",
              "maxJugadores": 2
            }
            """;

        MvcResult partidaRes = mockMvc.perform(post("/api/partidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partidaBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        JsonNode partidaJson = objectMapper.readTree(partidaRes.getResponse().getContentAsString());
        long partidaId = partidaJson.get("id").asLong();

        // 4) Unir jugador/barco a la partida (POST /api/partidas/{id}/join)
        String joinBody = """
            {
              "usuarioId": %d,
              "barcoId": %d
            }
            """.formatted(player.getId(), barcoId);

        MvcResult joinRes = mockMvc.perform(post("/api/partidas/{id}/join", partidaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(joinBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participantes[0].usuarioId").value(player.getId()))
                .andReturn();

        JsonNode estadoAfterJoin = objectMapper.readTree(joinRes.getResponse().getContentAsString());
        long participanteId = estadoAfterJoin.get("participantes").get(0).get("id").asLong();

        // 5) Iniciar partida (POST /api/partidas/{id}/start)
        MvcResult startRes = mockMvc.perform(post("/api/partidas/{id}/start", partidaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RUNNING"))
                .andReturn();

        JsonNode estadoRunning = objectMapper.readTree(startRes.getResponse().getContentAsString());
        assertThat(estadoRunning.get("estado").asText()).isEqualTo("RUNNING");

        // 6) Enviar un turno (POST /api/partidas/{id}/turno)
        //    Aceleramos en X = 1, Y = 0
        String turnoBody = """
            {
              "participanteId": %d,
              "accX": 1,
              "accY": 0
            }
            """.formatted(participanteId);

        MvcResult turnoRes = mockMvc.perform(post("/api/partidas/{id}/turno", partidaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(turnoBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RUNNING"))
                .andReturn();

        JsonNode estadoAfterTurno = objectMapper.readTree(turnoRes.getResponse().getContentAsString());
        JsonNode participanteJson = estadoAfterTurno.get("participantes").get(0);

        int velX = participanteJson.get("velX").asInt();
        int velY = participanteJson.get("velY").asInt();

        // Comprobamos que efectivamente cambió la velocidad / posición
        assertThat(velX).isNotZero();
        // velY puede seguir en 0 si solo aceleramos en X
    }
}
