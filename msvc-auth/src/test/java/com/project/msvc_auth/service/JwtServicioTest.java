package com.project.msvc_auth.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServicioTest {

    @Test
    void tokenIncluyeIdentificadorDeAuth() {

        JwtServicio servicio = new JwtServicio(
                "clave-de-desarrollo-de-al-menos-32-bytes",
                3600000
        );

        String token = servicio.generar(
                "usuarioPrueba",
                "USUARIO",
                7L
        );

        Claims datos = servicio.validar(token);

        assertEquals(
                "usuarioPrueba",
                datos.getSubject()
        );

        assertEquals(
                "USUARIO",
                datos.get("rol", String.class)
        );

        Number authId = (Number) datos.get("authId");

        assertEquals(7L, authId.longValue());
    }
}
