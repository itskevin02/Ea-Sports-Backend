package com.project.msvc_ranking.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtAcceso {

    private final SecretKey clave;

    public JwtAcceso(
            @Value("${app.jwt.secreto}") String secreto
    ) {
        clave = Keys.hmacShaKeyFor(
                secreto.getBytes(StandardCharsets.UTF_8)
        );
    }

    public void exigirOrganizador(String authorization) {

        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesion"
            );
        }

        Claims datos;

        try {
            datos = Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(
                            authorization.substring(7)
                    )
                    .getPayload();

        } catch (JwtException | IllegalArgumentException e) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token invalido o vencido"
            );
        }

        String rol = datos.get("rol", String.class);

        if (!"ADMIN".equals(rol) &&
                !"ORGANIZADOR".equals(rol)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permiso para gestionar rankings"
            );
        }
    }
}
