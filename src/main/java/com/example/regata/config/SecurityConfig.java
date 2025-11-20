package com.example.regata.config;

import com.example.regata.security.KeyProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,
                                    JwtDecoder jwtDecoder,
                                    JwtAuthenticationConverter jwtAuthConverter) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 👇 habilitamos CORS para usar la config del bean corsConfigurationSource()
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/**",
                                "/h2-console/**",
                                "/api/auth/**",   // login / register
                                "/oauth2/**"      // jwks
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> {
                            jwt.decoder(jwtDecoder);
                            jwt.jwtAuthenticationConverter(jwtAuthConverter);
                        })
                )
                .httpBasic(b -> b.disable())
                .formLogin(f -> f.disable())
                .headers(h -> h.frameOptions(fo -> fo.sameOrigin()));

        return http.build();
    }

    // ================= CORS GLOBAL =================
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Orígenes permitidos (frontend en Angular)
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:4200",
                "http://127.0.0.1:4200"
        ));

        // Métodos que permites desde el frontend
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Headers que aceptas
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin"
        ));

        // Si vas a usar cookies/sesiones, aquí true. Para solo Bearer, da igual.
        config.setAllowCredentials(true);

        // Headers que expones (opcional)
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica CORS a todo
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // =============== JWT ===============

    @Bean
    JwtDecoder jwtDecoder(KeyProvider keyProvider) {
        return NimbusJwtDecoder.withPublicKey(keyProvider.loadPublicKey()).build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles == null) {
                return Collections.emptyList();
            }
            return roles.stream()
                    .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });
        return converter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
