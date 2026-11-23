package com.project.msvc_registration.service;

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

    public Claims validar(String authorization) {

        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesion"
            );
        }

        try {

            Claims datos = Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(
                            authorization.substring(7)
                    )
                    .getPayload();

            String rol = datos.get("rol", String.class);

            if (!"ADMIN".equals(rol) &&
                    !"USUARIO".equals(rol) &&
                    !"USER".equals(rol)) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Rol sin permisos"
                );
            }

            return datos;

        } catch (JwtException | IllegalArgumentException e) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token invalido o vencido"
            );
        }
    }

    public boolean esAdmin(Claims datos) {
        return "ADMIN".equals(
                datos.get("rol", String.class)
        );
    }

    public Long authId(Claims datos) {

        Object id = datos.get("authId");

        if (!(id instanceof Number)) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "El token no contiene authId"
            );
        }

        return ((Number) id).longValue();
    }

    public void comprobarPropietario(
            Claims datos,
            Long propietario
    ) {

        if (esAdmin(datos)) {
            return;
        }

        if (propietario == null ||
                !propietario.equals(authId(datos))) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No puedes inscribir un equipo ajeno"
            );
        }
    }
}
