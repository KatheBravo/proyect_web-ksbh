package com.example.regata;

import com.example.regata.entity.Barco;
import com.example.regata.entity.ModeloBarco;
import com.example.regata.entity.Usuario;
import com.example.regata.repository.BarcoRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración: una por tipo de método HTTP
 * contra tus controladores REST reales.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // desactiva filtros de Security en tests
class RestIntegrationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UsuarioRepository usuarioRepo;

    @Autowired
    ModeloBarcoRepository modeloRepo;

    @Autowired
    BarcoRepository barcoRepo;

    // ========= POST =========
    @Test
    void post_creaModeloBarco_ok() throws Exception {
        String body = """
            {
              "nombre": "Modelo Test",
              "color": "#00FF00",
              "descripcion": "Modelo creado en test",
              "velMax": 3,
              "acelMax": 1,
              "maniobrabilidad": 90
            }
            """;

        mockMvc.perform(post("/api/modelos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Modelo Test"));
    }

    // ========= GET =========
    @Test
    void get_listaUsuarios_filtraPorRolePlayer() throws Exception {
        // DbInitializer ya creó 5 jugadores "Player 1..5"
        mockMvc.perform(get("/api/usuarios")
                        .param("role", "PLAYER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ========= PUT =========
    @Test
    void put_actualizaModeloBarco_ok() throws Exception {
        // Tomamos el primer modelo existente (creado por DbInitializer)
        List<ModeloBarco> modelos = modeloRepo.findAll();
        assertThat(modelos).isNotEmpty();
        Long id = modelos.get(0).getId();

        String body = """
            {
              "nombre": "Modelo Actualizado",
              "color": "#0000FF",
              "descripcion": "Descripcion actualizada",
              "velMax": 4,
              "acelMax": 1,
              "maniobrabilidad": 95
            }
            """;

        mockMvc.perform(put("/api/modelos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Modelo Actualizado"));
    }

    // ========= PATCH =========
    @Test
    void patch_actualizaParcialmenteUsuario_ok() throws Exception {
        // Tomamos cualquier usuario existente
        Usuario u = usuarioRepo.findAll().get(0);

        String body = """
            {
              "nombre": "Nombre Parcheado"
            }
            """;

        mockMvc.perform(patch("/api/usuarios/{id}", u.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(u.getId()))
                .andExpect(jsonPath("$.nombre").value("Nombre Parcheado"));
    }

    // ========= DELETE =========
    @Test
    void delete_eliminaBarco_ok() throws Exception {
        // Creamos un barco de prueba directamente por repositorio
        Usuario owner = usuarioRepo.findByEmail("player1@regata.com").orElseThrow();
        ModeloBarco modelo = modeloRepo.findAll().get(0);

        Barco b = new Barco();
        b.setNombre("Barco a borrar");
        b.setUsuario(owner);
        b.setModelo(modelo);
        b.setPosX(0);
        b.setPosY(0);
        b.setVelX(0);
        b.setVelY(0);
        b = barcoRepo.save(b);

        Long id = b.getId();

        // DELETE vía controlador
        mockMvc.perform(delete("/api/barcos/{id}", id))
                .andExpect(status().isNoContent());

        assertThat(barcoRepo.findById(id)).isEmpty();
    }
}
