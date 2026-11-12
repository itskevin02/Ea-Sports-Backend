package com.project.msvc_auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtServicio {

    private final SecretKey clave;
    private final long duracionMs;

    public JwtServicio(
            @Value("${app.jwt.secreto}") String secreto,
            @Value("${app.jwt.duracion-ms}") long duracionMs) {

        this.clave = Keys.hmacShaKeyFor(
                secreto.getBytes(StandardCharsets.UTF_8)
        );

        this.duracionMs = duracionMs;
    }

    public String generar(String username, String rol) {

        return Jwts.builder()
                .subject(username)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + duracionMs))
                .signWith(clave)
                .compact();
    }

    public Claims validar(String token) {

        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
