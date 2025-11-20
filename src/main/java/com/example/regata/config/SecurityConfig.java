package com.example.regata.config;

import com.example.regata.security.KeyProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/actuator/**",
                        "/h2-console/**",
                        "/api/auth/**",   // 👈 tu controlador está en /api/auth
                        "/oauth2/**"      // aquí expones /oauth2/jwks
                ).permitAll()
                .anyRequest().authenticated()
        );

        // Validación JWT con la public key
        http.oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> {
                    jwt.decoder(jwtDecoder);
                    jwt.jwtAuthenticationConverter(jwtAuthConverter);
                })
        );

        // evitar basic/form y permitir H2 console en frames
        http.httpBasic(b -> b.disable());
        http.formLogin(f -> f.disable());
        http.headers(h -> h.frameOptions(fo -> fo.sameOrigin()));

        return http.build();
    }

    // Decodificador JWT usando la PUBLIC KEY leída del KeyProvider
    @Bean
    JwtDecoder jwtDecoder(KeyProvider keyProvider) {
        return NimbusJwtDecoder.withPublicKey(keyProvider.loadPublicKey()).build();
    }

    // Cómo convertir el claim "roles" del JWT a authorities de Spring
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
