package com.example.regata.web;

import com.example.regata.security.JwtService;
import com.example.regata.security.KeyProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class JwksController {

    private final JwtService jwtService;
    private final KeyProvider keyProvider;

    public JwksController(JwtService jwtService, KeyProvider keyProvider) {
        this.jwtService = jwtService;
        this.keyProvider = keyProvider;
    }

    @GetMapping("/oauth2/jwks")
    public Map<String, Object> jwks() {
        return jwtService.buildJwks(keyProvider.loadPublicKey());
    }
}
