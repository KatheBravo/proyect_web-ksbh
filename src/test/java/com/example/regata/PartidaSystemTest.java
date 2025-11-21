package com.example.regata;

import com.example.regata.entity.PartidaEstado;
import com.example.regata.repository.BarcoRepository;
import com.example.regata.repository.UsuarioRepository;
import com.example.regata.web.dto.AuthResponse;
import com.example.regata.web.dto.partida.CreatePartidaRequest;
import com.example.regata.web.dto.partida.EstadoPartidaDto;
import com.example.regata.web.dto.partida.JoinPartidaRequest;
import com.example.regata.web.dto.partida.TurnoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PartidaSystemTest {

    @LocalServerPort
    int port;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private BarcoRepository barcoRepo;

    private WebTestClient client(String bearer) {
        WebTestClient.Builder builder = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port);
        if (bearer != null) {
            builder = builder.defaultHeader("Authorization", "Bearer " + bearer);
        }
        return builder.build();
    }

    @Test
    void flujo_de_partida_completo_por_http() {
        // ---- 1) Login Player1 ----
        var loginBody1 = """
            {
              "email": "player1@regata.com",
              "password": "Player1123"
            }
            """;

        AuthResponse loginRes1 = client(null)
                .post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginBody1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(loginRes1).isNotNull();
        String tokenPlayer1 = loginRes1.getToken();
        assertThat(tokenPlayer1).isNotBlank();

        // ---- 2) Crear partida ----
        CreatePartidaRequest createReq = new CreatePartidaRequest();
        createReq.setNombre("Partida HTTP");
        createReq.setMaxJugadores(4);

        EstadoPartidaDto creada = client(tokenPlayer1)
                .post().uri("/api/partidas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createReq)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EstadoPartidaDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(creada).isNotNull();
        Long partidaId = creada.getId();

        // ---- 3) Usuarios / barcos de la BD ----
        var player1 = usuarioRepo.findAll().stream()
                .filter(u -> "player1@regata.com".equalsIgnoreCase(u.getEmail()))
                .findFirst().orElseThrow();

        var player2 = usuarioRepo.findAll().stream()
                .filter(u -> "player2@regata.com".equalsIgnoreCase(u.getEmail()))
                .findFirst().orElseThrow();

        var barco1 = barcoRepo.findByUsuarioId(player1.getId()).get(0); // si usas Java 17
        var barco2 = barcoRepo.findByUsuarioId(player2.getId()).get(0);

        // ---- 4) Unirse Player1 ----
        JoinPartidaRequest join1 = new JoinPartidaRequest();
        join1.setUsuarioId(player1.getId());
        join1.setBarcoId(barco1.getId());

        EstadoPartidaDto estado = client(tokenPlayer1)
                .post().uri("/api/partidas/{id}/join", partidaId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(join1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EstadoPartidaDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(estado).isNotNull();
        assertThat(estado.getParticipantes()).hasSize(1);

        // ---- 5) Login Player2 + unirse ----
        var loginBody2 = """
            {"email":"player2@regata.com","password":"Player2123"}
            """;

        AuthResponse loginRes2 = client(null)
                .post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginBody2)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(loginRes2).isNotNull();
        String tokenPlayer2 = loginRes2.getToken();
        assertThat(tokenPlayer2).isNotBlank();

        JoinPartidaRequest join2 = new JoinPartidaRequest();
        join2.setUsuarioId(player2.getId());
        join2.setBarcoId(barco2.getId());

        estado = client(tokenPlayer2)
                .post().uri("/api/partidas/{id}/join", partidaId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(join2)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EstadoPartidaDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(estado).isNotNull();
        assertThat(estado.getParticipantes()).hasSize(2);
        assertThat(estado.getEstado()).isEqualTo(PartidaEstado.WAITING);

        // ---- 6) Iniciar partida ----
        estado = client(tokenPlayer1)
                .post().uri("/api/partidas/{id}/start", partidaId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EstadoPartidaDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(estado).isNotNull();
        assertThat(estado.getEstado()).isEqualTo(PartidaEstado.RUNNING);
    }
}
