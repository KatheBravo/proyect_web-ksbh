package com.example.regata.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class KeyProvider {

    private final ResourceLoader resourceLoader;

    // OJO: dejamos String para poder forzar prefijo "file:" si falta
    private final String privateKeyPath;
    private final String publicKeyPath;

    public KeyProvider(
            ResourceLoader resourceLoader,
            @Value("${jwt.private-key-path}") String privateKeyPath,
            @Value("${jwt.public-key-path}")  String publicKeyPath
    ) {
        this.resourceLoader = resourceLoader;
        this.privateKeyPath = normalizeLocation(privateKeyPath);
        this.publicKeyPath  = normalizeLocation(publicKeyPath);
    }

    private String normalizeLocation(String loc) {
        if (loc == null || loc.isBlank()) throw new IllegalStateException("jwt key path vacío");
        String lower = loc.toLowerCase();
        // Si no trae prefijo conocido, forzamos filesystem
        if (!(lower.startsWith("file:") || lower.startsWith("classpath:"))) {
            return "file:" + loc;
        }
        return loc;
    }

    private Resource getResource(String location) {
        return resourceLoader.getResource(location);
    }

    public RSAPrivateKey loadPrivateKey() {
        try (InputStream in = getResource(privateKeyPath).getInputStream()) {
            String pem = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            byte[] der = extractPem(pem, "PRIVATE KEY"); // PKCS#8 -> BEGIN PRIVATE KEY
            var kf = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(der));
        } catch (Exception e) {
            throw new RuntimeException("No pude leer la private key: " + e.getMessage(), e);
        }
    }

    public RSAPublicKey loadPublicKey() {
        try (InputStream in = getResource(publicKeyPath).getInputStream()) {
            String pem = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            byte[] der = extractPem(pem, "PUBLIC KEY"); // X.509 -> BEGIN PUBLIC KEY
            var kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(der));
        } catch (Exception e) {
            throw new RuntimeException("No pude leer la public key: " + e.getMessage(), e);
        }
    }

    private byte[] extractPem(String pem, String type) {
        String begin = "-----BEGIN " + type + "-----";
        String end   = "-----END " + type + "-----";
        int i = pem.indexOf(begin), j = pem.indexOf(end);
        if (i < 0 || j < 0) throw new IllegalArgumentException("PEM inválido (" + type + ")");
        String base64 = pem.substring(i + begin.length(), j).replaceAll("\\s", "");
        return Base64.getDecoder().decode(base64);
    }
}
