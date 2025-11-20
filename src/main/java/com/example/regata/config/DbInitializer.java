package com.example.regata.config;

import com.example.regata.entity.*;
import com.example.regata.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class DbInitializer {

    @Bean
    CommandLineRunner seedDatabase(UsuarioRepository usuarioRepo,
                                   ModeloBarcoRepository modeloRepo,
                                   BarcoRepository barcoRepo,
                                   MapaRepository mapaRepo,
                                   PasswordEncoder encoder) {
        return args -> {
            // --- 1) MAPA (único, si no existe) ---
            String[] lines = new String[] {
                    "XXXXXXXXXXXXXXXXXXXXXXXXXXXX",
                    "X..........................X",
                    "X..........................X",
                    "X..........XXXXXX..........X",
                    "X..........XXXXXX..........X",
                    "X..........XXXXXX..........X",
                    "X..........XXXXXX..........X",
                    "X..........XXXXXX..........X",
                    "X..........XXXXXX..........X",
                    "X..........XXXXXX..........X",
                    "X..........XXXXXX..........X",
                    "X..........XXXXXX..........X",
                    "X..........XXXXXX..........X",
                    "X..........XXXXXX..........X",
                    "X..........XXXXXX..........X",
                    "X..........XXXXXX..........X",
                    "X..........XXXXXX..........X",
                    "XPPPPPPPPPPXXXXXXmMMMMMMMMMX",
                    "XXXXXXXXXXXXXXXXXXXXXXXXXXXX"
            };
            if (!mapaRepo.existsByNombreIgnoreCase("Mapa Fijo")) {
                Mapa mapa = new Mapa();
                mapa.setNombre("Mapa Fijo");
                mapa.setLayout(String.join("\n", lines));
                mapaRepo.save(mapa);
            }

            // Precalcular posiciones 'P' (salidas)
            List<int[]> starts = new ArrayList<>();
            for (int r = 0; r < lines.length; r++) {
                String row = lines[r];
                for (int c = 0; c < row.length(); c++) {
                    if (row.charAt(c) == 'P') {
                        starts.add(new int[]{c, r}); // (posX=c, posY=r)
                    }
                }
            }
            if (starts.isEmpty()) {
                // fallback: esquina inferior izquierda del agua
                starts.add(new int[]{1, lines.length - 2});
            }

            // --- 2) USUARIOS ---
            if (usuarioRepo.count() == 0) {
                // 1 admin opcional
                Usuario admin = new Usuario();
                admin.setNombre("Admin");
                admin.setEmail("admin@regata.com");
                admin.setPasswordHash(encoder.encode("Admin123"));
                admin.setRole(Role.ADMIN);
                usuarioRepo.save(admin);

                // 5 players (requisito de la entrega)
                for (int i = 1; i <= 5; i++) {
                    Usuario u = new Usuario();
                    u.setNombre("Player " + i);
                    u.setEmail("player" + i + "@regata.com");
                    u.setPasswordHash(encoder.encode("Player" + i + "123"));
                    u.setRole(Role.PLAYER);
                    usuarioRepo.save(u);
                }
            }

            // --- 3) MODELOS ---
            if (modeloRepo.count() == 0) {
                Random rnd = new Random(7);
                for (int i = 1; i <= 10; i++) {
                    ModeloBarco m = new ModeloBarco();
                    m.setNombre("Modelo " + i);
                    m.setDescripcion("Modelo auto " + i);
                    // color del modelo
                    m.setColor(String.format("#%06X", rnd.nextInt(0xFFFFFF)));
                    // valores razonables
                    m.setVelMax(2 + rnd.nextInt(3));   // 2..4
                    m.setAcelMax(1);                   // 1
                    m.setManiobrabilidad(80 + rnd.nextInt(21)); // 80..100
                    modeloRepo.save(m);
                }
            }

            // --- 4) BARCOS ---
            if (barcoRepo.count() == 0) {
                List<Usuario> players = usuarioRepo.findAll().stream()
                        .filter(u -> u.getRole() == Role.PLAYER)
                        .toList();
                List<ModeloBarco> modelos = modeloRepo.findAll();
                if (players.size() < 5 || modelos.size() < 10) {
                    // si el user borró algo manualmente
                    return;
                }
                AtomicInteger idxStart = new AtomicInteger(0);
                Random rnd = new Random(13);

                for (Usuario u : players) {
                    for (int i = 1; i <= 10; i++) {
                        Barco b = new Barco();
                        b.setNombre("B-" + u.getNombre() + "-" + i);
                        b.setUsuario(u);
                        // modelo aleatorio
                        b.setModelo(modelos.get(rnd.nextInt(modelos.size())));

                        // colocar en una de las 'P' (recorrido circular)
                        int[] p = starts.get(idxStart.getAndIncrement() % starts.size());
                        b.setPosX(p[0]);
                        b.setPosY(p[1]);

                        // vel 0 al iniciar
                        b.setVelX(0);
                        b.setVelY(0);

                        barcoRepo.save(b);
                    }
                }
            }
        };
    }
}
