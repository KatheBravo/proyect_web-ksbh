package com.example.regata.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final KeyProvider keyProvider;

    @Value("${jwt.issuer}") private String issuer;
    @Value("${jwt.kid}")    private String kid;
    @Value("${jwt.access-token-ttl}") private String accessTtl;
    @Value("${jwt.refresh-token-ttl}") private String refreshTtl;

    // JwtService.java
    public String createAccessToken(Long userId,
                                    String email,
                                    Collection<String> roles,
                                    Map<String, Object> extra) {
        Instant now = Instant.now();
        Instant exp = now.plus(Duration.parse(accessTtl));
        PrivateKey pk = keyProvider.loadPrivateKey();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp))
                .subject(String.valueOf(userId))  // 👈 aquí lo paso a String para el "sub"
                .claim("email", email)
                .claim("roles", roles)
                .claim("extra", extra == null ? Collections.emptyMap() : extra)
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .keyID(kid)
                .build();

        try {
            SignedJWT jwt = new SignedJWT(header, claims);
            jwt.sign(new RSASSASigner(pk));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("No pude firmar el access token", e);
        }
    }


    public String createRefreshToken() {
        // Simple, opaco (se guarda en BD), no JWT
        return UUID.randomUUID().toString() + "." + UUID.randomUUID();
    }

    public Map<String, Object> buildJwks(RSAPublicKey pub) {
        RSAKey jwk = new RSAKey.Builder(pub)
                .keyUse(com.nimbusds.jose.jwk.KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(kid)
                .build();
        return new JWKSet(jwk).toJSONObject();
    }
}
